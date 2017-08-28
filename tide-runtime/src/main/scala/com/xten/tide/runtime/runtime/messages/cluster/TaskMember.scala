package com.xten.tide.runtime.runtime.messages.cluster


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/26 
  */
class TaskMember(val jobName:String, val taskId:String, val path :String, memberStatus: MemberStatus) extends Serializable{

}

