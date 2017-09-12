package com.xten.tide.runtime.runtime.appmaster

import java.net.URL
import java.util.UUID

import akka.actor.{ActorContext, ActorSelection}
import com.xten.tide.configuration.{ConfigConstants, Configuration}
import com.xten.tide.runtime.runtime.messages.cluster.{JobUpAction, Member, MemberRule, MemberStatus}
import com.xten.tide.runtime.runtime.messages.resource.NodeResource

import scala.collection.immutable.List
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Description: 
  * User: kongqingyu
  * Date: 2017/9/7 
  */
class JobRuntime(var taskMap : mutable.HashMap[String,TaskRuntime],configuration:Configuration){
  var jobId = UUID.randomUUID().toString
  var nodeResource :NodeResource = _
  var readyNum = 0
  var taskManagerSelection : ActorSelection = _

  /** Set of JAR files required to run this job. */
  private var userJars : List[URL]= List.empty

  /** List of classpaths required to run this job. */
  private var classpaths : List[String] = List.empty

  def setUserJars(userJars : List[URL]) = {
    this.userJars = userJars
  }


  def setJobManagerSelection(path :String)(implicit context:ActorContext) = {
    taskManagerSelection = context.actorSelection(path)

  }

  def addTask(task: TaskRuntime) = {
    taskMap.put(task.taskId,task)
  }

  def toJobUpAction(path:String) :JobUpAction = {

    configuration.setString(ConfigConstants.TASK_IPC_ADDRESS_KEY , nodeResource.ip)
    configuration.setInt(ConfigConstants.TASK_IPC_PORT_KEY , nodeResource.port)

    val tasks = taskMap.values.map(p => p.toTask()).toList
    JobUpAction(Member(jobId,MemberRule.JOB_MANAGER),path,tasks,configuration,userJars)
  }

  private def updateReadyNum() = {
    readyNum = taskMap.values.filter(p => !p.memberStatus.equals(MemberStatus.Up)).size
  }
}

object JobRuntime{


  def apply(tasks: ListBuffer[TaskRuntime], configuration: Configuration): JobRuntime ={
    val taskMap  = new  mutable.HashMap[String,TaskRuntime]
    tasks.map( p => {
      taskMap.put(p.taskId,p)
    })

    new JobRuntime(taskMap,configuration)

  }

  def apply(configuration: Configuration): JobRuntime ={
    new JobRuntime(new  mutable.HashMap[String,TaskRuntime],configuration)

  }

}


