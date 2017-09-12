package com.xten.tide.runtime.runtime.optimizer.node

import com.xten.tide.runtime.runtime.messages.cluster.NodeMember
import com.xten.tide.runtime.runtime.messages.resource.JobDispatch
import com.xten.tide.runtime.runtime.resourcemanager.NodeMemberRuntime
import com.xten.tide.runtime.runtime.resourcemanager.job.JobResourceDesc
import com.xten.tide.runtime.util.NetUtils

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

    for (i <- 2600 to 65536){

      if (!node.usedPorts.contains(i) && NetUtils.isIdlePort(node.ip,i)){
        node.usedPorts += i
        return  i
      }
    }
    throw new RuntimeException("can not find a idlePort")
  }

}
