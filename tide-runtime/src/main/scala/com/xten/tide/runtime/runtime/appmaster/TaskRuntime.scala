package com.xten.tide.runtime.runtime.appmaster

import java.util.UUID

import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.runtime.messages.cluster.{MemberStatus, Task}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/23 
  */
class TaskRuntime(val operatorId :String,
                  val operatorName : String,
                  val id :Int,
                  val className : String,
                  var logicalReceivers : List[String]) extends Serializable{


  val taskId : String = UUID.randomUUID().toString

  var executionConfig : Configuration = _

  var path : String = _

  var memberStatus : MemberStatus = MemberStatus.Init
  var receiverMap = new mutable.HashMap[String,ListBuffer[String]]()

  def up(path : String) = {
    this.path = path
    memberStatus = MemberStatus.Up
  }



  def toTask():Task = {
    new Task(id,className,logicalReceivers,taskId,executionConfig)
  }

  /**
    * 下游组件启动完成
    * @param name
    * @param path
    */
  def receiverUp(name:String ,path:String): Boolean = {
    var changed = false
    val paths = receiverMap.getOrElse(name, ListBuffer.empty[String])

    if (!paths.contains(path)){
      paths+= path
      changed = true
    }

    receiverMap.put(name,paths)
    changed
  }


}

object TaskRuntime{

  def transformTask(operatorRuntime: OperatorRuntime): Set[TaskRuntime] = {


    var tasks = new mutable.HashSet[TaskRuntime]

    for (i <- 1 to operatorRuntime.getParallelism()){

      val task = TaskRuntime(operatorRuntime,i)
      tasks.+=(task)
    }

    tasks.toSet

  }

  def apply(operatorRuntime: OperatorRuntime,id : Int): TaskRuntime ={

    new TaskRuntime(operatorRuntime.getOperatorId(),
      operatorRuntime.getOperatorName(),
      id,
      operatorRuntime.getClassName(),
      operatorRuntime.getOutOperatorIds)
  }
}
