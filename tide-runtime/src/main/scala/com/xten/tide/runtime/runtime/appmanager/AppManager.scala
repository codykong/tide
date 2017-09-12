package com.xten.tide.runtime.runtime.appmanager

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.xten.tide.configuration.{ClusterOptions, ConfigConstants, Configuration}
import com.xten.tide.runtime.runtime.akka.{AkkaUtils, TimeoutConstant}
import com.xten.tide.runtime.runtime.resourcemanager.ResourceManager
import com.xten.tide.runtime.util.NetUtils
import org.slf4j.LoggerFactory
import com.xten.tide.runtime.api.graph.ExecutionGraph
import com.xten.tide.runtime.runtime.messages.app.AppUpAction
import com.xten.tide.runtime.runtime.messages.resource._
import com.xten.tide.runtime.runtime.resourcemanager.job.JobResourceDesc
import akka.pattern.{AskTimeoutException, ask}
import com.xten.tide.runtime.runtime.exception.ResourceShortageException
import com.xten.tide.runtime.runtime.messages.ActionRes
import com.xten.tide.runtime.runtime.messages.cluster._

import scala.collection.mutable
import scala.concurrent.Await

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/21 
  */
class AppManager(configuration: Configuration) extends Actor{
  private val LOG = LoggerFactory.getLogger(AppManager.getClass)
  implicit val system = context.system

  var resourceManager : ActorRef = _
  var resourceManagerPath : String = _

  val appRuntimes = mutable.HashMap.empty[String,AppRuntime]

  override def preStart(): Unit = {
    super.preStart()
    LOG.info(s"AppManager has start : ${AkkaUtils.remotePath()}")

  }

  override def receive: Receive = {
    case action : AppUpAction => {

      var nodeResource: Option[NodeResource] = None

      try {
        val resourceDesc = new JobResourceDesc(action.executionGraph.appId)

        val request = AppMasterDispatchRequest(resourceDesc)

        implicit val timeout = TimeoutConstant.SYSTEM_MSG_TIMEOUT

        val resourceFuture = resourceManager ? request

        val resource = Await.result(resourceFuture, TimeoutConstant.SYSTEM_MSG_DURATION).asInstanceOf[JobDispatchResponse]


        if (resource.jobDispatches.length >= 1) {
          nodeResource = Some(resource.jobDispatches(0).nodeResource)
        }else {
          throw new ResourceShortageException("can not apply enough resource")
        }

        action.executionGraph.executionConfig.setString(ConfigConstants.TASK_IPC_ADDRESS_KEY, nodeResource.get.ip)
        action.executionGraph.executionConfig.setInt(ConfigConstants.TASK_IPC_PORT_KEY, nodeResource.get.port)

        val appRuntime = new AppRuntime(action.executionGraph,nodeResource.get)
        appRuntimes.put(action.executionGraph.appId, appRuntime)
        val nodeActorRef = AkkaUtils.pathToActorRef(nodeResource.get.path)

        val appManagerMember = Member.upMember(action.executionGraph.appId, MemberRule.APP_MANAGER)

        val jobUppedFuture = nodeActorRef ? AppMasterUpAction(appManagerMember, action.executionGraph, resourceManagerPath)
        val jobUpped = Await.result(jobUppedFuture, TimeoutConstant.SYSTEM_MSG_DURATION).asInstanceOf[JobUpped]


        if (jobUpped.code == ActionRes.ACTION_SUCCESS) {
          appRuntime.memberStatus = MemberStatus.Up
          appRuntime.appMasterActorRef = AkkaUtils.pathToActorRef(jobUpped.path)

          // 确认资源本消费，记账
          resourceManager ! ConsumedResource(appRuntime.nodeResource)
          appRuntime
        } else {
          // 把预定的资源释放
          resourceManager ! ReturnedResource(appRuntime.nodeResource)
          appRuntime.memberStatus = MemberStatus.Down
          LOG.error(s"AppManager started error,${jobUpped.jobId}")
        }
      } catch {
        case e : ResourceShortageException => {
          LOG.error(s"resource is not enough,${e.getMessage}")
        }
        case e : Exception => {
          LOG.error(e.getMessage,e)

          if (nodeResource.isDefined) {
            resourceManager ! ReturnedResource(nodeResource.get)
            if (appRuntimes.get(action.executionGraph.appId).isDefined) {
              appRuntimes.get(action.executionGraph.appId).get.memberStatus = MemberStatus.Down
            }

          }
        }
      }



    }

    case action : MemberUpAction => {

      action.member.rule match {
        case MemberRule.RESOURCE_MANAGER => {
          val resourceMember = action.member.asInstanceOf[ResourceMember]
          resourceManager = AkkaUtils.pathToActorRef(resourceMember.path)
          resourceManagerPath = resourceMember.path
        }

      }

    }
  }

}


object AppManager {

  private val LOG = LoggerFactory.getLogger(AppManager.getClass)

  val APP_MANAGER_NAME = "appmanager"

  private var appManager : ActorRef = null


  /**
    * 提交应用
    * @param executionGraph
    */
  def submitPlan(executionGraph: ExecutionGraph) = {

    if (appManager == null) {
      throw  new RuntimeException("AppMaster does not start")
    }

    appManager ! AppUpAction(executionGraph)


  }

  /**
    * 启动AppMaster 对应的ActorSystem 及 AppMasterActor
    * @param externalHostname
    * @param port
    * @param configuration
    * @return
    */
  def startActorSystemAndAppMangerActors(
                                          externalHostname: String,
                                          port: Int,
                                          configuration: Configuration
                                        ):(ActorSystem,ActorRef,ActorRef) = {

    val hostPort = NetUtils.unresolvedHostAndPortToNormalizedString(externalHostname,port)

    // Bring up the job manager actor system first, bind it to the given address.
    LOG.info(s"Starting AppManager actor system reachable at ${hostPort}")


    val appManagerSystem = try {
      val akkaConfig = AkkaUtils.getAkkaConfig(
        configuration,
        Some((externalHostname, port))
      )
      if (LOG.isDebugEnabled) {
        LOG.debug("Using akka configuration\n " + akkaConfig)
      }
      val clusterName = configuration.getString(ClusterOptions.CLUSTER_NAME_OPTIONS)
      AkkaUtils.createActorSystem(clusterName,akkaConfig)
    }
    catch {
      case t: Throwable =>
        if (t.isInstanceOf[org.jboss.netty.channel.ChannelException]) {
          val cause = t.getCause()
          if (cause != null && t.getCause().isInstanceOf[java.net.BindException]) {
            throw new Exception("Unable to create AppManager at address " + hostPort +
              " - " + cause.getMessage(), t)
          }
        }
        throw new Exception("Could not create AppManager actor system", t)
    }

    configuration.setString(ConfigConstants.MASTER_RPC_ADDRESS_KEY,externalHostname)
    configuration.setInt(ConfigConstants.MASTER_RPC_PORT_KEY,port)

    val (appManager,resourceManager) = startAppManagerActors(configuration,appManagerSystem)

    this.appManager = appManager


    (appManagerSystem,appManager,resourceManager)

  }

  /**
    * 启动AppMaster 及 ResourceManager
    * @param configuration
    * @param appManagerSystem
    * @return
    */
  def startAppManagerActors(configuration: Configuration ,
                           appManagerSystem : ActorSystem): (ActorRef,ActorRef) = {

    val appManagerClass = classOf[AppManager]

    val appManagerProps = getAppManagerProps(appManagerClass,
      configuration)

    val appManager = appManagerSystem.actorOf(appManagerProps,APP_MANAGER_NAME)

    // start ResourceManager
    val resourceManagerProps = ResourceManager.resourceManagerProps(configuration,AkkaUtils.remotePath(appManagerSystem,appManager))
    val resourceManager = ResourceManager.startResourceManager(appManagerSystem,resourceManagerProps)

    (appManager,resourceManager)

  }


  /**
    * 构造AppManager的Props
    * @param appManagerClass
    * @param configuration
    * @return
    */
  def getAppManagerProps(
                          appManagerClass: Class[_ <: AppManager],
                          configuration: Configuration
                        ):Props = {

    Props(appManagerClass,
      configuration)

  }

}

class AppRuntime(val executionGraph: ExecutionGraph,val nodeResource: NodeResource){
  var memberStatus : MemberStatus = MemberStatus.Init
  var appMasterActorRef : ActorRef =  _
}
