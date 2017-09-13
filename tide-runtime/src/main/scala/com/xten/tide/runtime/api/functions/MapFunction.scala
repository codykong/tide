package com.xten.tide.runtime.api.functions

import com.xten.tide.api.functions.BaseFunction
import com.xten.tide.runtime.api.event.IEvent

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/18 
  */
trait MapFunction extends BaseFunction{


  def map(event:IEvent):IEvent;



}
