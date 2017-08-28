package com.xten.tide.runtime.api.functions.sink

import com.xten.tide.api.functions.BaseFunction
import com.xten.tide.runtime.api.event.IEvent

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/27 
  */
trait SinkFunction extends BaseFunction{

  @throws[Exception]
  def invoke(value :IEvent)
}
