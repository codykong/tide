package com.xten.tide.runtime.runtime.component.execute

import com.xten.tide.runtime.api.event.IEvent
import com.xten.tide.runtime.api.functions.MapFunction
import com.xten.tide.runtime.runtime.component.{Component, ComponentContext}
import org.slf4j.LoggerFactory

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/23 
  */
class MapComponent(componentContext: ComponentContext[MapFunction])
  extends Component[MapFunction](componentContext){

  private final val LOG = LoggerFactory.getLogger(classOf[MapComponent])

  override def execute(input: IEvent): Unit = {
    val output = componentContext.function.map(input)
    emit(output)
  }

}
