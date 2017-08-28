package com.xten.tide.api.functions.sink

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/27 
  */
class PrintSinkBaseFunction[T] extends SinkBaseFunction[T]{

  @throws[Exception]
  override def invoke(value: T): Unit = {
    println(value)
  }
}
