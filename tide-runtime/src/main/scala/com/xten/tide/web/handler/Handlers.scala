package com.xten.tide.web.handler

import akka.actor.ActorSystem
import com.xten.tide.configuration.Configuration

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/25 
  */
object Handlers {

  private var configuration  : Option[Configuration] =  None
  private var actorSystem : Option[ActorSystem] = None

  def initHandlers(configuration : Configuration , actorSystem:ActorSystem ) = {

    this.configuration = Some(configuration)
    this.actorSystem = Some(actorSystem)
  }

  lazy val jarRunHandler = JarRunHandler.startJarRunHandlerActor(configuration.get,actorSystem.get)



}
