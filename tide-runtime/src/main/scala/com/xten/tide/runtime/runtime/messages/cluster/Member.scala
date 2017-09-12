package com.xten.tide.runtime.runtime.messages.cluster

import scala.collection.mutable.ListBuffer

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/22 
  */
class Member(val id : String) extends Serializable{
  var memberStatus : MemberStatus = MemberStatus.Init
  var rule : MemberRule = MemberRule.UNKNOWN

}

object Member {
  def upMember(id: String,rule : MemberRule): Member = {
    val member = new Member(id)
    member.rule = rule
    member.memberStatus = MemberStatus.Up

    member
  }

  def apply(id: String,rule : MemberRule): Member = {
    val member = new Member(id)
    member.rule = rule

    member
  }

  def apply(id: String,rule : MemberRule,memberStatus : MemberStatus): Member = {
    val member = new Member(id)
    member.rule = rule
    member.memberStatus = memberStatus

    member
  }
}

class MemberRule {}

object MemberRule {
  case object NODE_MANAGER extends MemberRule
  case object RESOURCE_MANAGER extends MemberRule
  case object APP_MANAGER extends MemberRule
  case object APP_MASTER extends MemberRule
  case object JOB_MANAGER extends MemberRule
  case object UNKNOWN extends MemberRule

}

class MemberStatus {}

object MemberStatus{

  case object Up extends MemberStatus
  case object Down extends MemberStatus
  case object Remove extends MemberStatus
  case object Init extends MemberStatus

}

class NodeMember(id :String,val ip: String,val path :String) extends Member(id){
  /**
    * 节点已经使用的端口
    */
  var usedPorts : ListBuffer[Int] = ListBuffer.empty
}

class ResourceMember(val path : String,name : String) extends Member(name)

class TaskMember(val jobName:String, val taskId:String, val path :String) extends Member(taskId)

class AppMasterMember(name : String, val path : String) extends Member(name)

class ResourceManagerMember(name : String, val path : String) extends Member(name)

class AppManagerMember(name : String, val path : String) extends Member(name)




