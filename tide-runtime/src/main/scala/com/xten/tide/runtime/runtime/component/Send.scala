package com.xten.tide.runtime.runtime.component

import akka.actor.ActorContext
import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.api.event.IEvent
import com.xten.tide.runtime.runtime.component.receiver.DistributionStrategy

/**
  * Description: 
  * User: kongqingyu
  * Date: 2017/9/13 
  */
trait Send {

  private var distribution : DistributionStrategy = null


  /**
    * 是否可以运行
    */
  var runnable = false


  def initDistributionStrategy(configuration: Configuration , receivers: List[String]): Unit = {
    distribution = DistributionStrategy(configuration,receivers)
  }


  def receiverChange(receivers : Map[String,List[String]])(implicit  context:ActorContext) = {
    val allReceiversStarted = distribution.changeReceivers(receivers)
    if (allReceiversStarted && !runnable){
      runnable = true
      startRun()
    }
  }

  /**
    * 发送事件
    * @param event
    * @return
    */
  def emit(event : IEvent) ={

    distribution.getSelections().map(p => {
      if (p.isDefined){
        p.get ! event
      }
    })

  }

  /**
    * 开始处理数据
    */
  def startRun() : Unit =()

  /**
    * 停止处理数据
    */
  def stopRun() : Unit = ()

}
