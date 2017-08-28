package com.xten.tide.runtime.runtime.optimizer.node

import com.xten.tide.runtime.runtime.messages.cluster.NodeMember
import com.xten.tide.runtime.runtime.messages.resource.JobDispatch
import com.xten.tide.runtime.runtime.resourcemanager.NodeMemberRuntime
import com.xten.tide.runtime.runtime.resourcemanager.job.{JobResourceDesc}

/**
  * Created with IntelliJ IDEA.
  * User: kongqingyu
  * Date: 2017/7/28 
  */
trait NodeDispatcher {

  /**
    * 将启动的Job分配到node中执行
    * @param nodes
    * @param taskResources
    */
  def dispatchJob(nodes : List[NodeMemberRuntime], taskResources: JobResourceDesc*) : List[JobDispatch]

  def dispatchJob(nodes : List[NodeMemberRuntime], taskResources: List[JobResourceDesc]) : List[JobDispatch]


  def newPort(node : NodeMember) : Int = {
    // todo mock 实现
    var newPort = 0
    if (node.usedPorts.size == 0) {
      newPort= 2660
    }else {
      newPort = node.usedPorts(node.usedPorts.size-1)
    }

    node.usedPorts += newPort

    newPort

  }

}
