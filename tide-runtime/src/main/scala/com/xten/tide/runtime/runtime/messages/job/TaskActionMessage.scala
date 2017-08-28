package com.xten.tide.runtime.runtime.messages.job

import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.runtime.jobgraph.Task
import com.xten.tide.runtime.runtime.messages.{ActionMessage, NoticeMessage}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/4 
  */


case class JobUpAction(jobId :String, appMasterPath:String, tasks: List[Task], configuration:Configuration) extends ActionMessage {
  private var values :String  = _
}

case class JobUpped(val jobId : String ,val path : String) extends NoticeMessage


