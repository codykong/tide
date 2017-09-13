package com.xten.tide.runtime.runtime.component

import com.xten.tide.api.functions.BaseFunction
import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.api.event.IEvent
import com.xten.tide.runtime.runtime.akka.{AkkaActor, AkkaUtils}
import com.xten.tide.runtime.runtime.component.receiver.{DistributionStrategy, ProximityDistributionStrategy}
import com.xten.tide.runtime.runtime.messages.ActionRes
import com.xten.tide.runtime.runtime.messages.cluster._
import org.slf4j.LoggerFactory

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/23 
  */
abstract class Component[T <: BaseFunction](componentContext: ComponentContext[T]) extends AkkaActor(componentContext.componentConfig){

  private final val LOG = LoggerFactory.getLogger(this.getClass)

  private var distribution : DistributionStrategy = DistributionStrategy(componentContext.componentConfig,componentContext.receivers)

  /**
    * 是否可以运行
    */
  var runnable = false

  /**
    * 启动完成之后，向上级报告
    */
  override def preStart(): Unit = {
    memberUp()
  }



  override def receive: Receive = {
    /**
      * 业务逻辑执行
      */
    case iEvent: IEvent => execute(iEvent)
    case action : ReceiverChangeAction => {


    }
  }


  def execute(event : IEvent): Unit


  private def memberUp() = {
    val member = new TaskMember(context.system.name,componentContext.taskId,AkkaUtils.remotePath)

    member.memberStatus = MemberStatus.Up
    context.parent ! TaskMemberUpped(member,ActionRes.ACTION_SUCCESS,ActionRes.ACTION_SUCCESS_MESSAGE)
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

