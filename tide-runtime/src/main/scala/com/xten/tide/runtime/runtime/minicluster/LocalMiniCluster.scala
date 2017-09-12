package com.xten.tide.runtime.runtime.minicluster

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}
import com.xten.tide.configuration.{ConfigConstants, Configuration}
import com.xten.tide.runtime.api.graph.ExecutionGraph
import com.xten.tide.runtime.runtime.akka.AkkaUtils
import com.xten.tide.runtime.runtime.appmaster.AppMaster
import com.xten.tide.runtime.runtime.nodemanager.{NodeContext, NodeManager}
import com.xten.tide.runtime.runtime.resourcemanager.{ResourceContext, ResourceManager}


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/23 
  */
class LocalMiniCluster(configuration: Configuration) extends MiniCluster(configuration) {


  var resourceManagerActorSystem = ResourceManager.startResourceManagerActorSystem(configuration)
  var resourceManager = ResourceManager.startResourceManager(resourceManagerActorSystem,ResourceContext.apply())

  Thread.sleep(1000)
  var nodeManagerActorSystem = NodeManager.startNodeManagerActorSystem(configuration,"127.0.0.1",2552)
  var nodeManager = NodeManager.startNodeManager(nodeManagerActorSystem,configuration)

  Thread.sleep(2000)


  override def startAppMaster(system: ActorSystem,executionGraph: ExecutionGraph): ActorRef = {

    val resourceManagerPath = AkkaUtils.remotePath(resourceManagerActorSystem,resourceManager)
    AppMaster.runAppMaster(Configuration.apply(),resourceManagerPath,system,executionGraph)

  }

  def startTaskManagerActorSystem(config:Config):ActorSystem = {
    AkkaUtils.createActorSystem(config)
  }
}

object LocalMiniCluster {
  def apply(configuration: Configuration): LocalMiniCluster = {
    new LocalMiniCluster(configuration)
  }

}
