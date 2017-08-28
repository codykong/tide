package com.xten.tide.runtime.runtime.messages.cluster

import akka.dispatch.ControlMessage
import com.xten.tide.runtime.runtime.jobgraph.Task
import com.xten.tide.runtime.runtime.messages.{ActionMessage, NoticeMessage, TideMessage}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/26 
  */

/**
  * 该Actor已经启动
  */
case class MemberUpped(member : TaskMember) extends NoticeMessage

/**
  * 该Action已经被移除
  */
case class MemberRemoved(member : TaskMember) extends NoticeMessage

/**
  * 启动Actor
  */
case class MemberUpAction(tasks: List[Task]) extends ActionMessage

/**
  * 移除Actor
  * @param task
  */
case class MemberRemoveAction(task: Task) extends ActionMessage

/**
  * 组件全量的下游信息
  * @param taskId 组件的id
  * @param receivers
  */
case class ReceiverChangeAction(taskId :String , receivers : Map[String,List[String]])
