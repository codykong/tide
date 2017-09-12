package com.xten.tide.runtime.runtime.component

import com.xten.tide.api.functions.BaseFunction
import com.xten.tide.runtime.api.event.IEvent
import com.xten.tide.runtime.runtime.akka.{AkkaActor, AkkaUtils}
import com.xten.tide.runtime.runtime.component.receiver.{DistributionStrategy, ProximityDistributionStrategy}
import com.xten.tide.runtime.runtime.messages.cluster._
import org.slf4j.LoggerFactory

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/23 
  */
abstract class Component[T <: BaseFunction](componentContext: ComponentContext[T]) extends AkkaActor(componentContext.componentConfig) {

  private final val LOG = LoggerFactory.getLogger(this.getClass)
  private val distribution = DistributionStrategy(componentContext.componentConfig,componentContext.receivers)

  /**
    * 是否可以运行
    */
  var runnable = false

  /**
    * 启动完成之后，向上级报告
    */
  override def preStart(): Unit = {
    LOG.info(s"component start ${AkkaUtils.remotePath()}")
    memberUp()
  }

  override def receive: Receive = {
    /**
      * 业务逻辑执行
      */
    case iEvent: IEvent => execute(iEvent)
    case action : ReceiverChangeAction => {
      val allReceiversStarted = distribution.changeReceivers(action.receivers)
      if (allReceiversStarted && !runnable){
        runnable = true
        startRun()
      }
    }
  }

  /**
    * 向下游发送事件
    * @param iEvent
    */
  //  def emit(iEvent : IEvent):Unit = {
  //    if (!iEvent.empty) {
  //      for ((_,rec) <- receiver){
  //        val path =  rec(0)
  //        context.system.actorSelection(path) ! iEvent
  //      }
  //    }
  //  }


  def execute(iEvent : IEvent): Unit


  private def memberUp() = {
    val member = new TaskMember(context.system.name,componentContext.taskId,AkkaUtils.remotePath)

    member.memberStatus = MemberStatus.Up
    context.parent ! TaskMemberUpped(member)
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
    * 停止处理数据
    */
  def stopRun() : Unit = ()

  /**
    * 开始处理数据
    */
  def startRun() : Unit =()






}

