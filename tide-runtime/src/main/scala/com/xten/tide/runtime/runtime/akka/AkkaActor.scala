package com.xten.tide.runtime.runtime.akka

import akka.actor.Actor
import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.api.event.IEvent
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/28 
  */
abstract class AkkaActor(config : Configuration) extends Actor{
  private val LOG: Logger = LoggerFactory.getLogger(classOf[AkkaActor])



}
