package com.xten.tide.runtime.runtime.messages.cluster

import com.xten.tide.runtime.runtime.messages.{ActionMessage, NoticeMessage, TideMessage}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/26 
  */

/**
  * 该Actor已经启动
  */
case class TaskMemberUpped(member : TaskMember) extends NoticeMessage

/**
  * 该Action已经被移除
  */
case class TaskMemberRemoved(member : TaskMember) extends NoticeMessage

/**
  * 启动Actor
  */
case class TaskMemberUpAction(tasks: List[Task]) extends ActionMessage

/**
  * 移除Actor
  * @param task
  */
case class TaskMemberRemoveAction(task: Task) extends ActionMessage

/**
  * 组件全量的下游信息
  * @param taskId 组件的id
  * @param receivers
  */
case class ReceiverChangeAction(taskId :String , receivers : Map[String,List[String]])
