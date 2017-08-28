package com.xten.tide.runtime.runtime.nodemanager

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory, ConfigValue}
import com.xten.tide.configuration.{ClusterOptions, ConfigConstants, Configuration}
import com.xten.tide.runtime.runtime.akka.{AkkaConfig, AkkaUtils, TimeoutConstant}
import com.xten.tide.runtime.runtime.messages.cluster.{MemberUpAction, NodeMember, NodeMemberUpAction}
import com.xten.tide.runtime.runtime.jobmanager.{JobManager, JobManagerContext}
import akka.pattern.{AskTimeoutException, ask}
import com.xten.tide.runtime.runtime.messages.ActionRes
import com.xten.tide.runtime.runtime.messages.job.{JobUpAction, JobUpped}
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

  var jobManagers :mutable.Map[String,String] = new mutable.HashMap[String,String]()

  var resourceManagerPathOption :Option[String] = None


  implicit val timeout = TimeoutConstant.TIMEOUT_2_SECONDS
  import context.dispatcher

  override def receive: Receive = {
    case  job:JobUpAction => {
      startJob(job)
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
  private def startJob(job: JobUpAction) ={


    val basicConfig = ConfigFactory.load(ConfigConstants.AKKA_REMOTE_BASIC_CONFIG_PATH)

    // 根据参数，组织启动所需的配置文件
    val executionConfig = this.executionConfig(job,basicConfig)

    var jobManagerActor:Option[ActorRef] = None

    if (jobManagers.get(job.jobId).isEmpty){
      // 启动ActorSystem
      val actorSystem = JobManager.startJobManagerActorSystem(job.jobId,executionConfig)
      // 启动job的任务管理Actor
      val jobManagerContext = JobManagerContext(job.appMasterPath)
      jobManagerActor = Some(JobManager.startJobManager(actorSystem,jobManagerContext))

      // 启动的JobManager做缓存
      jobManagers.put(job.jobId,AkkaUtils.remotePath(actorSystem,jobManagerActor.get))

    }else{

      val actorSelection = context.system.actorSelection(jobManagers.get(job.jobId).get)

      actorSelection.resolveOne().onComplete{
        case Success(ref) => jobManagerActor = Some(ref)
      }

    }
    // System 启动后，返回成功
    sender ! JobUpped(job.jobId,AkkaUtils.remotePath(context.system,jobManagerActor.get))


    // 启动所有的业务Component
    if (jobManagerActor.isDefined){
      ask(jobManagerActor.get,MemberUpAction(job.tasks))
    }else{
      throw new Exception("taskManagerActor is not started")
    }

  }

  def executionConfig(job: JobUpAction, basicConfig : Config) : Config = {
    val configMap= new mutable.HashMap[String,Any]()
    configMap.put(ConfigConstants.AKKA_REMOTE_NETTY_TCP_PORT,job.configuration.getInt(ConfigConstants.TASK_IPC_PORT_KEY,None))
    configMap.put(ConfigConstants.AKKA_REMOTE_NETTY_TCP_HOSTNAME,job.configuration.getString(ConfigConstants.TASK_IPC_ADDRESS_KEY,None))


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
