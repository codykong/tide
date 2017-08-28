package com.xten.tide.web.route

import com.xten.tide.runtime.runtime.messages.{ActionRes, SuccessActionRes}
import com.xten.tide.web.handler.{Handlers, JarStartAction}
import akka.pattern.ask
import akka.util.Timeout
import com.xten.tide.configuration.{ClusterOptions, ConfigConstants, Configuration}
import com.xten.tide.runtime.runtime.akka.TimeoutConstant

import scala.concurrent.Future

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/24 
  */

object DeployRoute {

  implicit val timeout = TimeoutConstant.SYSTEM_MSG_TIMEOUT

  var resourcePath : Option[String] = None

  def init(configuration: Configuration) = {
    val resourcePath : String = configuration.getString(ClusterOptions.LOCAL_RESOURCE_PATH_OPTIONS)

    // todo check is dir

    this.resourcePath = Some(resourcePath)
  }



  def run(tide : DeployTide) : Future[ActionRes] = {


    if (resourcePath.isEmpty){
      throw new RuntimeException("resourcePath has not bean configured")
    }


    val jarPath = s"${resourcePath.get}/${tide.jarName}"


    val jarStartAction = new JarStartAction(tide.name,tide.parallelism,tide.entryClass,jarPath)


    Handlers.jarRunHandler.ask(jarStartAction).mapTo[ActionRes]


  }




}