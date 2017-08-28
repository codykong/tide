package com.xten.tide.runtime.api.functions.source

import com.xten.tide.api.functions.BaseFunction
import com.xten.tide.runtime.api.event.IEvent


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/27 
  */
trait SourceFunction  extends BaseFunction {

  @throws[Exception]
  def run(ctx: SourceContext)

  def cancel()


}

trait ParallelSourceFunction extends SourceFunction {

}


trait SourceContext {


  def collect(element : IEvent)

  def take() : IEvent



}
