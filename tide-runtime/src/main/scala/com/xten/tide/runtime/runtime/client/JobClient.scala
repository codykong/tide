package com.xten.tide.runtime.runtime.client

import akka.actor.ActorSystem
import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.runtime.jobgraph.JobGraph

import scala.concurrent.duration.FiniteDuration

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/27 
  */
class JobClient {

}

object JobClient{

  def submitJobAndWait(config:Configuration,
                       actorSystem: ActorSystem,
                       jobGraph: JobGraph,
                       timeout:FiniteDuration)={


  }

  def submitJob(config:Configuration,
                actorSystem: ActorSystem,
                jobGraph: JobGraph,
                timeout:FiniteDuration)={

  }
}
