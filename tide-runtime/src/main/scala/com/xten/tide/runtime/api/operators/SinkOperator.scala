package com.xten.tide.runtime.api.operators

import com.xten.tide.runtime.api.functions.sink.SinkFunction
import com.xten.tide.runtime.api.functions.source.SourceFunction

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/23 
  */
class SinkOperator(val sinkFunction:SinkFunction) extends AbstractOperator(sinkFunction){


//  def run()={
//    sinkFunction.invoke(new BaseSourceContext)
//  }

}
