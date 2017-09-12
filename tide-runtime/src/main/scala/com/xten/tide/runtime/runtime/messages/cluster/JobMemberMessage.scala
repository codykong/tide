package com.xten.tide.runtime.runtime.messages.cluster

import java.net.URL

import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.api.graph.ExecutionGraph
import com.xten.tide.runtime.runtime.messages.{ActionMessage, ActionRes}

/**
  * Description: 
  * User: kongqingyu
  * Date: 2017/9/11 
  */



case class AppMasterUpAction(override val member : Member, executionGraph: ExecutionGraph,resourceManagerPath : String)
  extends MemberUpAction(member)


case class Task(id :Int,className : String,logicalReceivers : List[String],taskId : String,
                executionConfig : Configuration)

case class JobUpAction(override val member:Member, appMasterPath:String, tasks: List[Task], configuration:Configuration, userJars :List[URL])
  extends MemberUpAction(member) {
}

case class JobUpped(val jobId : String ,val path : String,code : Int,message : String) extends ActionRes(code ,message)
