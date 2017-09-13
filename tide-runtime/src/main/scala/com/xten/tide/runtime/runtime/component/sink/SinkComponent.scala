package com.xten.tide.runtime.runtime.component.sink

import com.xten.tide.runtime.api.event.IEvent
import com.xten.tide.runtime.api.functions.MapFunction
import com.xten.tide.runtime.api.functions.sink.SinkFunction
import com.xten.tide.runtime.runtime.component.{Component, ComponentContext}
import org.slf4j.LoggerFactory

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/23 
  */
class SinkComponent(componentContext: ComponentContext[SinkFunction])
  extends Component[SinkFunction](componentContext){

  private final val LOG = LoggerFactory.getLogger(classOf[SinkComponent])

  override def execute(event: IEvent): Unit = {
    componentContext.function.invoke(event)
  }

}
