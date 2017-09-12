package com.xten.tide.runtime.runtime.optimizer.node

import com.xten.tide.runtime.runtime.messages.resource.{JobDispatch, NodeResource}
import com.xten.tide.runtime.runtime.resourcemanager.NodeMemberRuntime
import com.xten.tide.runtime.runtime.resourcemanager.job.JobResourceDesc
import org.slf4j.LoggerFactory

import scala.collection.mutable


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/28 
  */
class JobBalanceNodeDispatcher extends NodeDispatcher{

  private val LOG =LoggerFactory.getLogger(classOf[JobBalanceNodeDispatcher])

  override def dispatchJob(nodes: List[NodeMemberRuntime], jobResources : JobResourceDesc*): List[JobDispatch] = {

    dispatchJob(nodes,jobResources.toList)

  }

  override def dispatchJob(nodes: List[NodeMemberRuntime], jobResources: List[JobResourceDesc]): List[JobDispatch] = {
    if (jobResources.size > nodes.size){
      throw new IllegalArgumentException("没有足够的节点可以分配Task")
    }

    var jobDispatchers = new mutable.ListBuffer[JobDispatch]

    val sortedNodes = nodes.sortBy(p => p.jobNum)

    for (i <- 0 until jobResources.length){
      sortedNodes(i).orderJobResource()

      val node = sortedNodes(i).nodeMember
      val nodeResource = NodeResource(node.id,node.path,newPort(node),node.ip)

      jobDispatchers.+=(JobDispatch(nodeResource,jobResources(i)))
    }

    jobDispatchers.toList
  }
}
