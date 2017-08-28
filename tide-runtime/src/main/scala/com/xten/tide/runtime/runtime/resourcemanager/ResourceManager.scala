package com.xten.tide.runtime.runtime.resourcemanager

import akka.actor.{Actor, ActorPath, ActorRef, ActorSystem, Props}
import com.typesafe.config.Config
import com.xten.tide.configuration.{ConfigConstants, Configuration}
import com.xten.tide.runtime.runtime.akka.AkkaUtils
import com.xten.tide.runtime.runtime.messages.ActionRes
import com.xten.tide.runtime.runtime.messages.cluster.{NodeMember, NodeMemberUpAction}
import com.xten.tide.runtime.runtime.messages.resource.{JobDispatchResponse, JobUpDispatchRequest}
import com.xten.tide.runtime.runtime.optimizer.node.JobBalanceNodeDispatcher
import com.xten.tide.runtime.util.AddressUtil
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/22 
  */
class ResourceManager(resourceContext: ResourceContext) extends Actor{

  val LOG = LoggerFactory.getLogger(classOf[ResourceManager])

  val resourceRegister = context.actorOf(Props.create(classOf[ResourceRegister]), name = ResourceRegister.RESOURCE_REGISTER)

  val nodeRuntimeMap = new mutable.HashMap[String,NodeMemberRuntime]()

  val nodeDispatcher = new JobBalanceNodeDispatcher

  override def preStart(): Unit = {
    super.preStart()
    LOG.info(s"ResourceManager has start : ${AkkaUtils.remotePath()}")
  }


  override def receive: Receive = {
    case action:NodeMemberUpAction => {
      val sender = context.sender()
      nodeRuntimeMap.put(action.nodeMember.name,new NodeMemberRuntime(action.nodeMember))
      sender ! ActionRes.defaultSuccess()

    }
    case request:JobUpDispatchRequest =>{

      val sender = context.sender()
      val dispatchers = nodeDispatcher.dispatchJob(nodeRuntimeMap.values.toList,request.jobs)

      sender ! JobDispatchResponse(dispatchers)
    }
  }
}


object ResourceManager {

  private val LOG = LoggerFactory.getLogger(ResourceManager.getClass)

  val RESOURCE_MANAGER_NAME = "resourceManager"

  def startResourceManagerActorSystem(config : Configuration) :ActorSystem = {
    val resourceManagerAkkaConfig = getResourceManagerAkkaConfig(config)
    AkkaUtils.createActorSystem(RESOURCE_MANAGER_NAME,resourceManagerAkkaConfig)
  }

  def startResourceManager(system: ActorSystem,resourceContext: ResourceContext): ActorRef = {
    val ref = system.actorOf(Props.create(classOf[ResourceManager],resourceContext), name = ResourceManager.RESOURCE_MANAGER_NAME)
    ref
  }

  def startResourceManager(system: ActorSystem,resourceManagerProps: Props): ActorRef = {
    val ref = system.actorOf(resourceManagerProps, name = ResourceManager.RESOURCE_MANAGER_NAME)
    ref
  }

  def resourceManagerProps(configuration:Configuration,appManagerPath: String) : Props = {

    val resourceContext = ResourceContext(configuration,appManagerPath)

    Props(classOf[ResourceManager],
      resourceContext)
  }

  def getResourceManagerAkkaConfig(configuration: Configuration): Config ={

    val port = configuration.getInt(ConfigConstants.RESOURCE_MANAGER_IPC_PORT_KEY,
      Some(ConfigConstants.DEFAULT_RESOURCE_MANAGER_IPC_PORT))
    val host = configuration.getString(ConfigConstants.RESOURCE_MANAGER_IPC_ADDRESS_KEY,
      Some(AddressUtil.getHostIP))

    AkkaUtils.getAkkaConfig(configuration, Some((host, port)))

  }

  /**
    * 通过hostAndPort中获取path信息
    * @param hostAndPort
    * @return
    */
  def pathFromHostAndPort(hostAndPort: String,actorSystemName : String):String = {

    s"akka.tcp://${actorSystemName}@${hostAndPort}/user/${RESOURCE_MANAGER_NAME}"

  }



}


class NodeMemberRuntime(val nodeMember:NodeMember ){

  var taskNum = 0
  var components = 0


}
