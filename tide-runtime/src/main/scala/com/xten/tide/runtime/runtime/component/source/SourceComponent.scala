package com.xten.tide.runtime.runtime.component.source

import com.xten.tide.runtime.api.event.{EmptyEvent, IEvent}
import com.xten.tide.runtime.api.functions.source.SourceFunction
import com.xten.tide.runtime.api.operators.BaseSourceContext
import com.xten.tide.runtime.runtime.akka.TimeoutConstant
import com.xten.tide.runtime.runtime.component.{Component, ComponentContext}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/1 
  */
class SourceComponent(componentContext: ComponentContext[SourceFunction])
  extends Component[SourceFunction](componentContext){

  val sourceContext = new BaseSourceContext()

  override def preStart(): Unit = {
    super.preStart()
    // 持续拉去已经产生的数据
    takeAndEmit()
  }



  override def execute(input: IEvent): Unit = {
    componentContext.function.run(sourceContext)

  }


  /**
    * 从结果中取出事件，并发送
    */
  def takeAndEmit(): Unit ={
    context.system.scheduler.scheduleOnce(TimeoutConstant.DURATION_0_SECONDS) {
      while (true){
        val event = sourceContext.take()
        emit(event)
      }
    }
  }

  override def stopRun(): Unit = {

  }

  override def startRun(): Unit = {
    execute(new EmptyEvent)
  }
}