package com.xten.tide.runtime.api.datastream

import com.xten.tide.runtime.api.operators.SinkOperator
import com.xten.tide.runtime.api.transformations.{SinkTransformation, Transformation}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/23 
  */
class DataStreamSink(operator: SinkOperator, inputStream : DataStream) {

  var transformation : SinkTransformation = new SinkTransformation("Unnamed",inputStream.getExecutionEnvironment().getParallelism,operator,inputStream.transformation)


}
