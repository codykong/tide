package com.xten.tide.runtime.runtime.messages.cluster

import com.xten.tide.runtime.runtime.messages.{ActionMessage, NoticeMessage}

/**
  * Description: 
  * User: kongqingyu
  * Date: 2017/9/8 
  */
/**
  * member启动报告
  */
class MemberUpped(res : Boolean) extends NoticeMessage

/**
  * member移除报告
  */
class MemberRemoved(member : Member) extends NoticeMessage

/**
  * member启动
  */
class MemberUpAction(val member: Member) extends ActionMessage

/**
  * member移除
  * @param member
  */
class MemberRemoveAction(member: Member) extends ActionMessage
