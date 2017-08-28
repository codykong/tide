package com.xten.tide.runtime.runtime.component.receiver

import akka.actor.{ActorContext, ActorSelection}

import scala.collection.mutable

/**
  * 就近分发策略
  * User: kongqingyu
  * Date: 2017/8/2 
  */
abstract class GroupingDistributionStrategy(logicalReceivers: List[String]) extends DistributionStrategy{

  val receivers = initLogicalReceivers(logicalReceivers)
  /**
    * 获取需要发送的ActorSelection
    *
    * @return
    */
  override def getSelections(): List[Option[ActorSelection]] = {
    null
  }

  /**
    * 初始化逻辑接受者
    * @param logicalReceivers
    * @return
    */
  private def initLogicalReceivers(logicalReceivers : List[String]) : mutable.Map[String,List[String]] = {
    val receiversMap : mutable.Map[String,List[String]] =new mutable.HashMap()

    for (receiver <- logicalReceivers) {
      receiversMap.put(receiver,List.empty)
    }
    receiversMap
  }

  /**
    * 修改下游
    *
    * @param receivers 全量的下游路径
    */
  override def changeReceivers(receivers: Map[String, List[String]])(implicit context: ActorContext): Boolean = ???
}
