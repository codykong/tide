package com.xten.tide.runtime.runtime.akka

import java.util

import akka.actor.{ActorContext, ActorPath, ActorRef, ActorSelection, ActorSystem, ExtendedActorSystem, Extension, ExtensionKey, Props}
import com.typesafe.config.{Config, ConfigFactory}
import com.xten.tide.api.functions.BaseFunction
import com.xten.tide.configuration.{ConfigConstants, Configuration}
import com.xten.tide.runtime.runtime.component.ComponentContext
import org.jboss.netty.logging.{InternalLoggerFactory, Slf4JLoggerFactory}
import org.slf4j.LoggerFactory

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/5 
  */
object AkkaUtils {

  val LOG = LoggerFactory.getLogger(AkkaUtils.getClass)


  private def getRemoteAkkaConfig():Config = {

    val config = ConfigFactory.load(ConfigConstants.AKKA_REMOTE_BASIC_CONFIG_PATH)

//    ConfigFactory.parseString(config)

//    ConfigFactory.load(ConfigFactory.empty()).withFallback(config)

    config
  }

  def createActorSystem(name:String, akkaConfig: Config): ActorSystem = {
    // Initialize slf4j as logger of Akka's Netty instead of java.util.logging (FLINK-1650)
    InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory)
    ActorSystem.create(name, akkaConfig)
  }

  def createActorSystem(akkaConfig: Config): ActorSystem = {
    createActorSystem("defaultTide",akkaConfig)
  }

  def createComponent[T <: BaseFunction](context: ActorContext,proxyClass :Class[_],  componentContext: ComponentContext[T]) : ActorRef = {
    context.actorOf(Props.create(proxyClass, componentContext), componentContext.taskId)
  }

  def remotePath(system: ActorSystem, ref: ActorRef): String = {
    val remoteAddr = RemoteAddressExtension(system).address
    val remotePath = ref.path.toStringWithAddress(remoteAddr)
    remotePath
  }

  def remotePath()(implicit actorContext: ActorContext) :String = {
    remotePath(actorContext.system,actorContext.self)
  }


  def remotePath(system: ActorSystem, actorPath: ActorPath): String = {
    val remoteAddr = RemoteAddressExtension(system).address
    val remotePath = actorPath.toStringWithAddress(remoteAddr)
    remotePath
  }

  /**
    * path 转为对应的Selection
    * @param path
    * @param context
    * @return
    */
  def pathToSelection(path:String)(implicit context: ActorContext) : ActorSelection = {
    context.actorSelection(path)
  }

  def pathToSelection(paths:List[String])(implicit context: ActorContext) : List[ActorSelection] = {
    paths.map(p => {context.actorSelection(p)})
  }


  def getAkkaConfig(configuration: Configuration,
                    externalAddress: Option[(String, Int)]): Config = {
//    val defaultConfig = getBasicAkkaConfig(configuration)
    val defaultConfig = getRemoteAkkaConfig

    externalAddress match {

      case Some((hostname, port)) =>

        val remoteConfig = parseConfig((ConfigConstants.AKKA_REMOTE_NETTY_TCP_HOSTNAME,hostname),
          (ConfigConstants.AKKA_REMOTE_NETTY_TCP_PORT,port))

        remoteConfig.withFallback(defaultConfig)

      case None =>
        defaultConfig
    }
  }

  def parseConfig(args: (String,Any)*) : Config={
    val map = new util.HashMap[String,Any]()

    for (arg <- args){
      map.put(arg._1,arg._2)
    }
    ConfigFactory.parseMap(map)
  }






  def getLogLevel: String = {
    if (LOG.isTraceEnabled) {
      "TRACE"
    } else {
      if (LOG.isDebugEnabled) {
        "DEBUG"
      } else {
        if (LOG.isInfoEnabled) {
          "INFO"
        } else {
          if (LOG.isWarnEnabled) {
            "WARNING"
          } else {
            if (LOG.isErrorEnabled) {
              "ERROR"
            } else {
              "OFF"
            }
          }
        }
      }
    }
  }

}

class RemoteAddressExtensionImpl(system: ExtendedActorSystem) extends Extension {
  def address = system.provider.getDefaultAddress
}

object RemoteAddressExtension extends ExtensionKey[RemoteAddressExtensionImpl] {

}
