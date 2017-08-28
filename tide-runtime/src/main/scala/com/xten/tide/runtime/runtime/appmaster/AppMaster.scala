package com.xten.tide.runtime.runtime.appmaster

import java.util.UUID

import akka.actor.{Actor, ActorContext, ActorSelection, ActorSystem, Props}
import com.xten.tide.configuration.{ConfigConstants, Configuration}
import com.xten.tide.runtime.runtime.akka.{AkkaUtils, TimeoutConstant}
import com.xten.tide.runtime.runtime.optimizer.BalanceAppOptimizer
import akka.pattern.ask
import com.xten.tide.runtime.api.graph.StreamGraph
import com.xten.tide.runtime.runtime.jobgraph.Task
import com.xten.tide.runtime.runtime.messages.app.AppUpAction
import com.xten.tide.runtime.runtime.messages.cluster.{MemberStatus, MemberUpped, NodeResource, ReceiverChangeAction}
import com.xten.tide.runtime.runtime.messages.job.{JobUpAction, JobUpped}
import com.xten.tide.runtime.runtime.messages.resource.{JobDispatchResponse, JobUpDispatchRequest}
import com.xten.tide.runtime.runtime.resourcemanager.job.JobResourceDesc
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/27 
  */
class AppMaster(appMasterContext: AppMasterContext) extends Actor{

  val LOG = LoggerFactory.getLogger(classOf[AppMaster])

  val resourceMangerSelection = AkkaUtils.pathToSelection(appMasterContext.resourceManger)

  val jobGraphStore : mutable.HashMap[String,JobGraph] = new mutable.HashMap[String,JobGraph]()

  var streamGraph : StreamGraph = _

  var appStatus :MemberStatus = MemberStatus.Init

  override def receive: Receive = {
    case action :AppUpAction => {
      streamGraph = action.streamGraph

      val jobGraphs  =  BalanceAppOptimizer.appUp(action)

      val jobResources = jobGraphs.map(p => new JobResourceDesc(p.jobId))
      implicit val timeout = TimeoutConstant.SYSTEM_MSG_TIMEOUT
      val responseFuture = resourceMangerSelection ? JobUpDispatchRequest(jobResources)

      val dispatches = Await.result(responseFuture, TimeoutConstant.SYSTEM_MSG_DURATION)
        .asInstanceOf[JobDispatchResponse].jobDispatches
        .map(p => (p.jobResource.jobId,p.nodeResource)).toMap

      jobGraphs.map(p => {
        p.nodeResource = dispatches.get(p.jobId).get
        jobGraphStore.put(p.jobId,p)
        (p.nodeResource,p.toJobUpAction(AkkaUtils.remotePath()))
      }).map( p => {
        LOG.info(s"taskUpActions is ${p._2}")

        val future = AkkaUtils.pathToSelection(p._1.path) ? p._2
        future.onComplete{
          case  member: Success[JobUpped]=> {
            jobGraphStore.get(member.value.jobId).get.setJobManagerSelection(member.value.path)
          }
          case Failure(failure) => {
            LOG.error(s"taskUpActions error,task is ${p._2},error is ${failure}")
          }
        }
      })

    }
    case member: MemberUpped => {
      // 组件启动成功
      val task = jobGraphStore.get(member.member.jobName).get.taskMap.get(member.member.taskId).get
      task.up(member.member.path)
      syncReceiverMap(task)
      streamGraph.streamNodes.get(task.streamNodeId).get.getInNodes()
        .foreach(p =>{
          // 获取所有发送给该task的StreamNode
          streamGraph.streamNodes.get(p).get.getTasks().foreach(inTask => {
            // 修改该StreamNode 的下的所有task的下游组件map
            val receiverPaths = inTask.receiverMap.get(task.taskId).getOrElse(ListBuffer.empty[String])
            if (!receiverPaths.contains(member.member.path)){
              receiverPaths += member.member.path
              // 如果该下游组件运行中，则同步路由
              inTask.receiverMap.put(task.streamNodeId,receiverPaths)
              syncReceiverMap(inTask)

            }
          })
        })
//      jobGraphStore.values.flatMap(p => p.taskMap.values)
//        .filter(p => p.receivers.contains(member.member.taskId))
//        .map(p => {
//          val changed = p.receiverUp(member.member.taskId,member.member.path)
//          if (changed && p.memberStatus.equals(MemberStatus.Up)){
//            val receivers = p.receiverMap.map(p => (p._1,p._2.toList)).toMap
//
//          }
//        })
    }
  }


  /**
    * 同步task下的下游
    * @param task
    */
  private def syncReceiverMap(task : Task) ={

    if (task.memberStatus.equals(MemberStatus.Up) && !task.receiverMap.isEmpty){
      val receiverMap = task.receiverMap.map(p => (p._1,p._2.toList)).toMap
      context.actorSelection(task.path) ! ReceiverChangeAction(task.taskId,receiverMap)
    }
  }

  /**
    * 检查job的状态是否需要变更
    */
//  private def jobStatusChange() = {
//    var readyComponentNum = 0
//    var totalComponentNum = 0
//    jobMemberMap.values.map(p =>{
//      totalComponentNum += p.componentRuntimeMap.size
//      readyComponentNum += p.readyComponentNum
//    })
//
//    if (readyComponentNum == totalComponentNum && jobStatus.equals(MemberStatus.Init)){
//      jobStatus = MemberStatus.Up
//      jobReady()
//
//    }
//  }

  /**
    * job已经就绪
    */
  private def jobReady() = {


  }



}

object AppMaster {
  val DEFAULT_APP_MASTER_NAME = "appMaster"

  def runAppMaster(config: Configuration,resourceMangerPath : String,system: ActorSystem) = {

    val appMasterContext: AppMasterContext = AppMasterContext(config,resourceMangerPath)
    system.actorOf(Props.create(classOf[AppMaster], appMasterContext), DEFAULT_APP_MASTER_NAME)

  }

}

class JobGraph(var taskMap : mutable.HashMap[String,Task],configuration:Configuration){
  var jobId = UUID.randomUUID().toString
  var nodeResource :NodeResource = _
  var readyNum = 0
  var taskManagerSelection : ActorSelection = _

  def setJobManagerSelection(path :String)(implicit context:ActorContext) = {
    taskManagerSelection = context.actorSelection(path)

  }

  def addTask(task: Task) = {
    taskMap.put(task.taskId,task)
  }

  def toJobUpAction(path:String) :JobUpAction = {

    configuration.setString(ConfigConstants.TASK_IPC_ADDRESS_KEY , nodeResource.ip)
    configuration.setInt(ConfigConstants.TASK_IPC_PORT_KEY , nodeResource.port)

    val tasks = taskMap.values.toList
    JobUpAction(jobId,path,tasks,configuration)
  }

  private def updateReadyNum() = {
    readyNum = taskMap.values.filter(p => !p.memberStatus.equals(MemberStatus.Up)).size
  }
}

object JobGraph{


  def apply(tasks: ListBuffer[Task], configuration: Configuration): JobGraph ={
    val taskMap  = new  mutable.HashMap[String,Task]
    tasks.map( p => {
      taskMap.put(p.taskId,p)
    })

    new JobGraph(taskMap,configuration)

  }

  def apply(configuration: Configuration): JobGraph ={
    new JobGraph(new  mutable.HashMap[String,Task],configuration)

  }

}

class AppGraph{


}


class ComponentRuntime(val componentNode: Task){


  var path : String = _
  var memberStatus : MemberStatus = MemberStatus.Init
  var receiverMap = new mutable.HashMap[String,ListBuffer[String]]()



}


