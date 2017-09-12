package com.xten.tide.runtime.runtime.minicluster

import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.Config
import com.xten.tide.configuration.{ConfigConstants, Configuration}
import com.xten.tide.runtime.api.graph.{ExecutionGraph, StreamGraph}
import com.xten.tide.runtime.runtime.akka.{AkkaUtils, TimeoutConstant}
import com.xten.tide.runtime.runtime.messages.app.AppUpAction
import com.xten.tide.runtime.util.AddressUtil

import scala.util.control.Breaks._

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/27 
  */
abstract class MiniCluster(val userConfiguration:Configuration) {



  var appMasterActorSystem : ActorSystem = null
  var appMasterActor : ActorRef = null


  def start(executionGraph: ExecutionGraph)= {

    appMasterActorSystem = startAppMasterActorSystem()
    appMasterActor = startAppMaster(appMasterActorSystem,executionGraph)

  }

    val numTaskManagers = userConfiguration.getInt(
    ConfigConstants.LOCAL_NUMBER_TASK_MANAGER,
    Some(ConfigConstants.DEFAULT_LOCAL_NUMBER_TASK_MANAGER)
  )



  def startAppMasterActorSystem(): ActorSystem = {
    val config = getAppMasterAkkaConfig()
    val jobManagerName = userConfiguration.getString(ConfigConstants.JOB_MANAGER_ACTOR_SYSTEM_NAME,
      Some("LocalJobManger"))
    AkkaUtils.createActorSystem(jobManagerName ,config)
  }

  def getAppMasterAkkaConfig(): Config ={


    val host = userConfiguration.getString(ConfigConstants.APP_MASTER_IPC_ADDRESS_KEY,
      Some(AddressUtil.getHostIP))


    var portOption :Option[Int] = None

    breakable{
      for(i <- ConfigConstants.DEFAULT_JOB_MANAGER_IPC_PORT to ConfigConstants.DEFAULT_JOB_MANAGER_IPC_PORT_MAX){
        if (AddressUtil.isIdlePort(host,i)){
          portOption = Some(i)
          break()

        }
      }
    }

    if (portOption.isEmpty) {
      throw new Exception("there is no port available")
    }

    val port = userConfiguration.getInt(ConfigConstants.APP_MASTER_IPC_PORT_KEY,
      Some(portOption.get))

    AkkaUtils.getAkkaConfig(userConfiguration, Some((host, port)))

  }


  def startAppMaster(system: ActorSystem,executionGraph: ExecutionGraph): ActorRef

  def submitApp(executionGraph: ExecutionGraph) = {

    implicit val timeout = TimeoutConstant.SYSTEM_MSG_TIMEOUT
    appMasterActor ! AppUpAction(executionGraph)

  }

}
