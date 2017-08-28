package com.xten.tide.runtime.runtime.component.receiver

import akka.actor.{ActorContext, ActorSelection}

import scala.collection.mutable
import scala.util.Random

/**
  * 随机发送策略
  * User: kongqingyu
  * Date: 2017/8/2 
  */
class RandomDistributionStrategy(logicalReceivers: List[String]) extends DistributionStrategy{

  // 下游的集合
  var receivers : mutable.Map[String,List[ActorSelection]] = initLogicalReceivers(logicalReceivers)

  val DEFAULT_RANDOM = new Random

  /**
    * 获取需要发送的ActorSelection
    * @return
    */
  override def getSelections(): List[Option[ActorSelection]] = {
    val selections = receivers.map(p => getSelection(p._2)).toList
    selections
  }

  /**
    * 修改下游
    *
    * @param newReceivers 全量的下游路径
    */
  override def changeReceivers(newReceivers: Map[String, List[String]])(implicit context: ActorContext): Boolean = {
    newReceivers.map(p => {
      val selections = p._2.map(p => context.actorSelection(p))
      receivers.put(p._1,selections)
    })

    receivers.filter(p => p._2.isEmpty).isEmpty
  }


  /**
    * 初始化逻辑接受者
    * @param logicalReceivers
    * @return
    */
  private def initLogicalReceivers(logicalReceivers : List[String]) : mutable.Map[String,List[ActorSelection]] = {
    val receiversMap : mutable.Map[String,List[ActorSelection]] =new mutable.HashMap()

    for (receiver <- logicalReceivers) {
      receiversMap.put(receiver,List.empty[ActorSelection])
    }
    receiversMap
  }


  private def getSelection(selections :List[ActorSelection]):Option[ActorSelection] ={
    var selection :Option[ActorSelection] = None

    if (!selections.isEmpty){
      val index = DEFAULT_RANDOM.nextInt(selections.length)
      selection = Some(selections(index))
    }

    selection
  }


}
