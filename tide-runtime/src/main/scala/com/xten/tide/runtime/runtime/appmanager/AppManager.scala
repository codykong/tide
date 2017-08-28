package com.xten.tide.runtime.runtime.appmanager

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.xten.tide.configuration.{ClusterOptions, ConfigConstants, Configuration}
import com.xten.tide.runtime.runtime.akka.AkkaUtils
import com.xten.tide.runtime.runtime.resourcemanager.ResourceManager
import com.xten.tide.runtime.util.NetUtils
import org.slf4j.LoggerFactory
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/21 
  */
class AppManager(configuration: Configuration) extends Actor{
  private val LOG = LoggerFactory.getLogger(AppManager.getClass)
  implicit val system = context.system

  override def preStart(): Unit = {
    super.preStart()
    LOG.info(s"AppManager has start : ${AkkaUtils.remotePath()}")

  }

  override def receive: Receive = {
    case _ => {

    }
  }

}


object AppManager {

  private val LOG = LoggerFactory.getLogger(AppManager.getClass)

  val APP_MANAGER_NAME = "appmanager"

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
