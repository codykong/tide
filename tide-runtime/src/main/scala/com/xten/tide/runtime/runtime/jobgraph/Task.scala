package com.xten.tide.runtime.runtime.jobgraph

import java.util.UUID

import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.api.graph.StreamNode
import com.xten.tide.runtime.runtime.messages.cluster.MemberStatus

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/23 
  */
class Task(val streamNodeName :String,val streamNodeId : String,val id :Int) extends Serializable{

  var jobVertexClass : Class[_ <: Any] = _

  val taskId : String = UUID.randomUUID().toString

  var executionConfig : Configuration = _

  var path : String = _

  var receivers : List[String] = _

  var memberStatus : MemberStatus = MemberStatus.Init
  var receiverMap = new mutable.HashMap[String,ListBuffer[String]]()

  def up(path : String) = {
    this.path = path
    memberStatus = MemberStatus.Up
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

object Task{

  def transformTask(streamNode :StreamNode): Set[Task] = {

//    val componentNodes : mutable.Map[Int,ComponentNode] = new mutable.HashMap()

    var tasks = new mutable.HashSet[Task]

    for (i <- 1 to streamNode.parallelism){
      val task = new Task(streamNode.name,streamNode.streamNodeId,i)

      task.jobVertexClass = streamNode.jobVertexClass
      task.receivers = streamNode.getOutNodes()

      tasks.+=(task)
    }

    tasks.toSet

  }
}
