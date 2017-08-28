package com.xten.tide.runtime.runtime.resourcemanager

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.xten.tide.runtime.runtime.messages.ActionRes
import com.xten.tide.runtime.runtime.messages.cluster.{MemberStatus, NodeMember, NodeMemberRemoveAction, NodeMemberUpAction}
import com.xten.tide.runtime.runtime.messages.resource.{AllLiveNodesRequest, AllLiveNodesResponse}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/22 
  */
class ResourceRegister() extends Actor{
  val LOG = LoggerFactory.getLogger(classOf[ResourceRegister])

  /**
    * 所有注册的节点
    */
  val nodes = new mutable.HashMap[String,NodeMember]()

  override def receive: Receive = {
    case action:NodeMemberUpAction => {
      val sender = context.sender()
      nodes.put(action.nodeMember.name,action.nodeMember)
      sender ! ActionRes.defaultSuccess()

    }
    case action:NodeMemberRemoveAction => {
      nodes.remove(action.nodeMember.name)
    }
    case AllLiveNodesRequest => {
      val sender = context.sender()
      sender ! new AllLiveNodesResponse(allLiveNodes())
    }
  }

  /**
    * 所有存活的节点
    * @return
    */
  private def allLiveNodes() : List[NodeMember]={

    val liveNodes = new ListBuffer[NodeMember]
    nodes.values.filter(v => v.memberStatus.equals(MemberStatus.Up)).foreach( v => liveNodes+= v)
    liveNodes.toList
  }
}

object ResourceRegister{
  val RESOURCE_REGISTER = "resourceRegister"

  /**
    * 通过hostAndPort中获取path信息
    * @param hostAndPort
    * @return
    */
  def pathFromHostAndPort(hostAndPort: String):String = {

    s"akka.tcp://${ResourceManager.RESOURCE_MANAGER_NAME}@${hostAndPort}/user/${ResourceRegister.RESOURCE_REGISTER}"

  }

}
