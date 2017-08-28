package com.xten.tide.api.functions.sink

import com.xten.tide.api.functions.BaseFunction

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/27 
  */
trait SinkBaseFunction[T] extends BaseFunction{

  @throws[Exception]
  def invoke(value :T)
}
