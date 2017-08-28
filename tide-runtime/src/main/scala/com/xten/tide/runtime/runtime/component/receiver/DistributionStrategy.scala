package com.xten.tide.runtime.runtime.component.receiver

import akka.actor.{ActorContext, ActorSelection}
import com.xten.tide.configuration.Configuration

/**
  * 分发策略
  * User: kongqingyu
  * Date: 2017/8/2 
  */
trait DistributionStrategy {

  /**
    * 获取需要发送的ActorSelection
    * @return
    */
  def getSelections() : List[Option[ActorSelection]]

  /**
    * 同步下游资源，并评估下游是否已启动
    * @param receivers 全量的下游路径
    */
  def changeReceivers(receivers : Map[String,List[String]])(implicit context: ActorContext) : Boolean


}

object DistributionStrategy{

  def apply(configuration: Configuration,logicalReceivers: List[String]): DistributionStrategy = {
    new RandomDistributionStrategy(logicalReceivers)
  }

}
