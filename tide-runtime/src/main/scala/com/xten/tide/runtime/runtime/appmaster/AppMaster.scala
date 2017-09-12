package com.xten.tide.runtime.runtime.appmaster

import java.util.UUID

import akka.actor.{Actor, ActorContext, ActorRef, ActorSelection, ActorSystem, Props}
import com.xten.tide.configuration.{ConfigConstants, Configuration}
import com.xten.tide.runtime.runtime.akka.{AkkaUtils, TimeoutConstant}
import com.xten.tide.runtime.runtime.optimizer.BalanceAppOptimizer
import akka.pattern.ask
import com.xten.tide.runtime.api.graph.{ExecutionGraph, ExecutionNode, StreamGraph}
import com.xten.tide.runtime.runtime.messages.app.AppUpAction
import com.xten.tide.runtime.runtime.messages.cluster._
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

  private val LOG = LoggerFactory.getLogger(classOf[AppMaster])

  val resourceMangerSelection = AkkaUtils.pathToSelection(appMasterContext.resourceManger)

  val jobRuntimes : mutable.HashMap[String,JobRuntime] = new mutable.HashMap[String,JobRuntime]()

  var appRuntime : AppRuntime = _

  var appStatus :MemberStatus = MemberStatus.Init

  override def preStart(): Unit = {
    super.preStart()
    LOG.info(s"AppMaster start ,id is ${appMasterContext.executionGraph.appId}")
    appUp(appMasterContext.executionGraph)

  }

  override def receive: Receive = {
    case member: TaskMemberUpped => {
      // 组件启动成功
      val task = jobRuntimes.get(member.member.jobName).get.taskMap.get(member.member.taskId).get
      task.up(member.member.path)
      syncReceiverMap(task)

      appRuntime.operatorRuntimes.get(task.operatorId).get.getInOperatorIds
        .foreach(p => {
          // 获取所有发送给该task的Operator
          appRuntime.operatorRuntimes.get(p).get.getTaskRuntimes.foreach(inTask => {
            // 修改该StreamNode 的下的所有task的下游组件map
            val receiverPaths = inTask.receiverMap.get(task.taskId).getOrElse(ListBuffer.empty[String])

            if (!receiverPaths.contains(member.member.path)){
              receiverPaths += member.member.path
              // 如果该下游组件运行中，则同步路由
              inTask.receiverMap.put(task.operatorId,receiverPaths)
              syncReceiverMap(inTask)

            }
          })
        })

    }
  }

  /**
    * 启动app
    * @param executionGraph
    * @return
    */
  private def appUp(executionGraph: ExecutionGraph) = {
    appRuntime = AppRuntime(executionGraph)

    val jobRuntime  =  BalanceAppOptimizer.appUp(appRuntime)

    val jobResources = jobRuntime.map(p => new JobResourceDesc(p.jobId))
    implicit val timeout = TimeoutConstant.SYSTEM_MSG_TIMEOUT
    val responseFuture = resourceMangerSelection ? JobUpDispatchRequest(jobResources)

    val dispatches = Await.result(responseFuture, TimeoutConstant.SYSTEM_MSG_DURATION)
      .asInstanceOf[JobDispatchResponse].jobDispatches
      .map(p => (p.jobResource.jobId,p.nodeResource)).toMap

    jobRuntime.map(p => {
      p.setUserJars(executionGraph.getUserJars())
      p.nodeResource = dispatches.get(p.jobId).get
      jobRuntimes.put(p.jobId,p)
      (p.nodeResource,p.toJobUpAction(AkkaUtils.remotePath()))
    }).map( p => {
      LOG.info(s"taskUpActions is ${p._2}")

      val future = AkkaUtils.pathToSelection(p._1.path) ? p._2
      future.onComplete{
        case  member: Success[JobUpped]=> {
          jobRuntimes.get(member.value.jobId).get.setJobManagerSelection(member.value.path)
        }
        case Failure(failure) => {
          LOG.error(s"taskUpActions error,task is ${p._2},error is ${failure}")
        }
      }
    })
  }


  /**
    * 同步task下的下游
    * @param task
    */
  private def syncReceiverMap(task : TaskRuntime) ={

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

  def runAppMaster(config: Configuration,resourceMangerPath : String,system: ActorSystem,executionGraph: ExecutionGraph)
    : ActorRef = {

    val appMasterContext: AppMasterContext = AppMasterContext(config,resourceMangerPath,executionGraph)
    system.actorOf(Props.create(classOf[AppMaster], appMasterContext), DEFAULT_APP_MASTER_NAME)

  }

}


