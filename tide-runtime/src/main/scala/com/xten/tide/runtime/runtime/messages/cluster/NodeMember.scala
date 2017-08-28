package com.xten.tide.runtime.runtime.messages.cluster

import scala.collection.mutable.ListBuffer


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/22 
  */
class NodeMember(val host :String,val ip: String,val path :String) extends Serializable{

  /**
    * 节点的状态
    */
  var memberStatus: MemberStatus = MemberStatus.Up
  /**
    * 节点已经使用的端口
    */
  var usedPorts : ListBuffer[Int] = ListBuffer.empty
  val name = host

}

case class NodeResource(val name :String ,val path : String , val port : Int ,val ip :String)
