package com.xten.tide.runtime.api.datastream

import com.xten.tide.runtime.api.environment.ExecutionEnvironment
import com.xten.tide.runtime.api.operators.SourceOperator
import com.xten.tide.runtime.api.transformations.{SourceTransformation, Transformation}
import com.xten.tide.utils.Preconditions

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/28 
  */

/**
  *
  * @param isParallel 是否是并行数据源
  * @param environment
  * @param transformation
//  * @tparam T
  */
class DataStreamSource(isParallel :Boolean , environment:ExecutionEnvironment,transformation:Transformation) extends DataStream(environment,transformation){

  if (!isParallel){
    setParallelism(1)
  }

  def setParallelism(parallelism: Int): DataStreamSource = {
    if (parallelism > 1 && !isParallel) {
      throw new IllegalArgumentException("Source: " + transformation.getId + " is not a parallel source")
    }else {
      Preconditions.checkArgument(parallelism > 0, "The parallelism of an operator must be at least 1.")
//      Preconditions.checkArgument(parallelism == 1, "The parallelism of non parallel operator must be 1.")
      transformation.setParallelism(parallelism)

      this
    }
  }



}

object DataStreamSource{

  def apply(isParallel: Boolean, environment: ExecutionEnvironment, operator :SourceOperator,sourceName:String): DataStreamSource = {

    val sourceTransformation =new SourceTransformation(sourceName,environment.getParallelism,operator)
    new DataStreamSource(isParallel, environment, sourceTransformation)
  }
}
