package com.xten.tide.runtime.runtime.messages.cluster

import com.xten.tide.runtime.runtime.messages.{ActionMessage, NoticeMessage}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/22 
  */

/**
  * 该Node已经启动
  */
case class NodeMemberUpped(member : Boolean) extends NoticeMessage

/**
  * 该Action已经被移除
  */
case class NodeMemberRemoved(member : NodeMember) extends NoticeMessage

/**
  * 启动Node
  */
case class NodeMemberUpAction(nodeMember: NodeMember) extends ActionMessage

/**
  * 移除Node
  * @param nodeMember
  */
case class NodeMemberRemoveAction(nodeMember: NodeMember) extends ActionMessage
