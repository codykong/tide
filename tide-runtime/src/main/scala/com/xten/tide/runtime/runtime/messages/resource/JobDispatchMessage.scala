package com.xten.tide.runtime.runtime.messages.resource

import com.xten.tide.runtime.runtime.messages.cluster.NodeResource
import com.xten.tide.runtime.runtime.messages.{RequestMessage, ResponseMessage}
import com.xten.tide.runtime.runtime.resourcemanager.job.{JobResourceDesc}

/**
  * 基于Task数量均衡的Node分配策略
  * User: kongqingyu
  * Date: 2017/7/28 
  */

/**
  * 元素为每个Job 的数量
  * @param jobs
  */
case class JobUpDispatchRequest(jobs : List[JobResourceDesc]) extends RequestMessage


case class JobDispatch(nodeResource : NodeResource, jobResource: JobResourceDesc)


case class JobDispatchResponse(jobDispatches : List[JobDispatch]) extends ResponseMessage
