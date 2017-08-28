package com.xten.tide.web

import akka.actor.ActorSystem
import com.xten.tide.configuration.Configuration
import com.xten.tide.web.handler.Handlers
import com.xten.tide.web.route.{DeployRoute, RestApi}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/28 
  */
object WebMonitor {

  def start(actorSystem: ActorSystem,configuration: Configuration,host:String,webPort : Int) = {

    Handlers.initHandlers(configuration,actorSystem)

    DeployRoute.init(configuration)

    RestApi.bind(actorSystem,host,webPort,configuration)


  }



}
