package com.xten.tide.runtime.api.functions.sink

import com.xten.tide.runtime.api.event.IEvent

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/27 
  */
class PrintSinkFunction extends SinkFunction{

  @throws[Exception]
  override def invoke(event: IEvent): Unit = {
    println(s"${this} receive and print ${event}")
  }
}
