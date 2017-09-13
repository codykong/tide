package com.xten.tide.runtime.runtime.nodemanager

import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory, ConfigValue}
import com.xten.tide.configuration.{ClusterOptions, ConfigConstants, Configuration}
import com.xten.tide.runtime.runtime.akka.{AkkaConfig, AkkaUtils, TimeoutConstant}
import com.xten.tide.runtime.runtime.messages.cluster._
import com.xten.tide.runtime.runtime.jobmanager.{JobManager, JobManagerContext}
import akka.pattern.{AskTimeoutException, ask}
import com.xten.tide.client.program.JobWithJars
import com.xten.tide.runtime.api.graph.ExecutionGraph
import com.xten.tide.runtime.runtime.appmaster.AppMaster
import com.xten.tide.runtime.runtime.messages.ActionRes
import com.xten.tide.runtime.runtime.resourcemanager.{ResourceManager, ResourceRegister}
import com.xten.tide.runtime.util.AddressUtil
import org.slf4j.LoggerFactory

import scala.collection.{JavaConverters, mutable}
import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration
import scala.util.Success
import scala.util.control.Breaks

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/27 
  */
class NodeManager(nodeContext: NodeContext) extends Actor{

  private val LOG = LoggerFactory.getLogger(classOf[NodeManager])

  var runtimeJobs :mutable.Map[String,AbstractRuntimeJob] = new mutable.HashMap[String,AbstractRuntimeJob]()

  var resourceManagerPathOption :Option[String] = None


  implicit val timeout = TimeoutConstant.TIMEOUT_2_SECONDS
  import context.dispatcher

  override def receive: Receive = {
    case  job:JobUpAction => {
      val runtimeJob = new RuntimeJob(job.appMasterPath,job.member.id,job.member.id,job.configuration,job.tasks,job.userJars)
      startJob(runtimeJob)
    }
    case action : AppMasterUpAction => {

      val appManagerPath = AkkaUtils.remotePath(context.system,context.sender())
      val runtimeAppMaster = new RuntimeAppMaster(appManagerPath,action.member.id,action.member.id,
        action.executionGraph.executionConfig,action.resourceManagerPath,action.executionGraph)

      startJob(runtimeAppMaster)
    }
  }

  override def preStart(): Unit = {
    super.preStart()
    registerNode
  }

  /**
    * 注册节点
    */
  private def registerNode(): Unit ={

    val seedNodes = nodeContext.config.getStringList(ConfigConstants.MASTER_SEED_NODES_RPC_KEY)


    if (seedNodes.isEmpty){
      throw new Exception("has no seedNode in config")
    }

    val nodeMember = new NodeMember(AddressUtil.getHostName,AddressUtil.getHostIP, AkkaUtils.remotePath())

    val clusterName = nodeContext.config.getString(ClusterOptions.CLUSTER_NAME_OPTIONS)

    Breaks.breakable{
      for(hostAndPort <- seedNodes){
        val resourceManagerPath = ResourceManager.pathFromHostAndPort(hostAndPort.asInstanceOf[String],clusterName)

        val future = context.system.actorSelection(resourceManagerPath) ? NodeMemberUpAction(nodeMember)

        var connected = false
        try {
          val actionRes = Await.result(future, TimeoutConstant.SYSTEM_MSG_DURATION).asInstanceOf[ActionRes]
          connected = actionRes.isSuccess()
        } catch {
          case e :  AskTimeoutException => {
            LOG.info(s"can`t connect to master seed-node ${hostAndPort},${e.getMessage}",e)
          }
        }

        if (connected){
          resourceManagerPathOption = Some(resourceManagerPath)
          Breaks.break()
        }

      }

    }

    if (resourceManagerPathOption.isEmpty) {
      throw new RuntimeException(s"get no register response from resource manager,${seedNodes}")
    }



  }



  /**
    * 启动Job
    * @param job
    */
  private def startJob(job: AbstractRuntimeJob) ={

    val sender = context.sender()

    val basicConfig = ConfigFactory.load(ConfigConstants.AKKA_REMOTE_BASIC_CONFIG_PATH)

    // 根据参数，组织启动所需的配置文件
    val executionConfig = this.executionConfig(job.configuration,basicConfig)

    var jobManagerActor:Option[ActorRef] = None
    var jobManagerPath : Option[String] = None

    if (runtimeJobs.get(job.jobId).isEmpty){

      if (job.isInstanceOf[RuntimeJob]){

        val runtimeJob = job.asInstanceOf[RuntimeJob]
        val parentClassLoader = this.getClass.getClassLoader

        val userClassLoader = JobWithJars.buildUserCodeClassLoader(runtimeJob.userJars,List.empty,parentClassLoader)
        Thread.currentThread().setContextClassLoader(userClassLoader)

        // 启动ActorSystem
        val actorSystem = JobManager.startJobManagerActorSystem(job.jobId,executionConfig)
        // 启动job的任务管理Actor
        val jobManagerContext = new JobManagerContext(job.managerPath,runtimeJob.tasks)
        jobManagerActor = Some(JobManager.startJobManager(actorSystem,jobManagerContext))

        jobManagerPath = Some(AkkaUtils.remotePath(actorSystem,jobManagerActor.get))
        Thread.currentThread().setContextClassLoader(parentClassLoader)
      }else {
        // 启动ActorSystem
        val actorSystem = JobManager.startJobManagerActorSystem(job.jobId,executionConfig)
        val appRuntime = job.asInstanceOf[RuntimeAppMaster]
        jobManagerActor = Some(AppMaster.runAppMaster(job.configuration,appRuntime.resourcePath,actorSystem,appRuntime.executionGraph))
        jobManagerPath = Some(AkkaUtils.remotePath(actorSystem,jobManagerActor.get))
      }

      job.jobMonitorRef = jobManagerActor.get

      // 启动的JobManager做缓存
      runtimeJobs.put(job.jobId,job)

    }else{

      jobManagerActor = Some(runtimeJobs.get(job.jobId).get.jobMonitorRef)
    }
    // System 启动后，返回成功
    val jobUpped = JobUpped(job.jobId,jobManagerPath.get,ActionRes.ACTION_SUCCESS,"")
    sender ! jobUpped

  }

  def executionConfig(configuration: Configuration, basicConfig : Config) : Config = {
    val configMap= new mutable.HashMap[String,Any]()
    configMap.put(ConfigConstants.AKKA_REMOTE_NETTY_TCP_PORT,configuration.getInt(ConfigConstants.TASK_IPC_PORT_KEY,None))
    configMap.put(ConfigConstants.AKKA_REMOTE_NETTY_TCP_HOSTNAME,configuration.getString(ConfigConstants.TASK_IPC_ADDRESS_KEY,None))


    val remoteConfig = ConfigFactory.parseMap(JavaConverters.mapAsJavaMap(configMap))
    remoteConfig.withFallback(basicConfig)
  }



}


object NodeManager{

  val NODE_MANAGER_NAME = "nodemanager"

  def startNodeManagerActorSystem(config : Configuration,hostname : String ,port : Int) :ActorSystem = {
    val nodeManagerAkkaConfig = getNodeManagerAkkaConfig(config,hostname,port)
    val clusterName = config.getString(ClusterOptions.CLUSTER_NAME_OPTIONS)
    AkkaUtils.createActorSystem(clusterName,nodeManagerAkkaConfig)
  }

  def startNodeManager(system: ActorSystem,configuration: Configuration): ActorRef = {
    val props = getNodeManagerProps(classOf[NodeManager],configuration)
    system.actorOf(props, NODE_MANAGER_NAME)
  }

  /**
    * 构造NodeManager的Props
    * @param appManagerClass
    * @param configuration
    * @return
    */
  def getNodeManagerProps(
                          appManagerClass: Class[_ <: NodeManager],
                          configuration: Configuration
                        ):Props = {

    val nodeContext = NodeContext(configuration)
    Props(appManagerClass,
      nodeContext)

  }

  def getNodeManagerAkkaConfig(configuration : Configuration,hostname : String ,port : Int): Config ={

    AkkaUtils.getAkkaConfig(configuration, Some((hostname, port)))

  }


}

class AbstractRuntimeJob(val managerPath : String ,val jobId : String,val jobName : String,val configuration : Configuration){

  var jobMonitorRef : ActorRef = null

}

class RuntimeJob(appMasterPath : String , jobId : String,jobName : String,configuration : Configuration,
                 val tasks: List[Task],
                 val userJars : List[URL])
  extends AbstractRuntimeJob(appMasterPath , jobId ,jobName,configuration)

class RuntimeAppMaster(appManagerPath : String , appId : String,appName : String,configuration : Configuration,
                       val resourcePath : String,val executionGraph: ExecutionGraph)
  extends AbstractRuntimeJob(appManagerPath , appId ,appName,configuration)
