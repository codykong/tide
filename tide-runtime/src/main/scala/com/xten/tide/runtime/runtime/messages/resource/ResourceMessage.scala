package com.xten.tide.runtime.runtime.messages.resource

import com.xten.tide.runtime.runtime.messages.cluster.NodeMember
import com.xten.tide.runtime.runtime.messages.{NoticeMessage, RequestMessage, ResponseMessage, TideMessage}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/22 
  */
class ResourceMessage extends TideMessage{

}

case class AllLiveNodesRequest() extends RequestMessage

case class AllLiveNodesResponse(node :List[NodeMember]) extends ResponseMessage


case class NodeResource(val id :String ,val path : String , val port : Int ,val ip :String) extends NoticeMessage

case class ConsumedResource(node : NodeResource) extends NoticeMessage

case class ReturnedResource(node : NodeResource) extends NoticeMessage
