package com.xten.tide.runtime.api.operators

import java.util.concurrent.LinkedBlockingQueue

import com.xten.tide.runtime.api.event.IEvent
import com.xten.tide.runtime.api.functions.source.SourceContext

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/3 
  */
class BaseSourceContext extends SourceContext{

  val queue = new LinkedBlockingQueue[IEvent]


  override def collect(element: IEvent): Unit = {
    queue.put(element)
  }

  override def take(): IEvent = {
    queue.take()
  }
}
