package com.xten.tide.runtime.api.transformations

import com.xten.tide.runtime.api.operators.{SinkOperator, SourceOperator}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/23 
  */
class SinkTransformation(name : String, parallelism :Int,operator :SinkOperator,val input : Transformation) extends Transformation(name,parallelism,operator){

  def getInput = this.input
}
