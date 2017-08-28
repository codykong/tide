package com.xten.tide.runtime.api.transformations

import com.google.common.base.Preconditions
import com.xten.tide.api.functions.BaseFunction
import com.xten.tide.runtime.api.operators.Operator

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/28 
  */
class Transformation(var name : String, var parallelism :Int,protected var operator :Operator) {


  protected final var id : Int = Transformation.getNewNodeId
  Preconditions.checkNotNull(name)

  /**
    * The maximum parallelism for this stream transformation. It defines the upper limit for
    * dynamic scaling and the number of key groups used for partitioned state.
    */
  protected var maxParallelism : Int =_

  protected var uid : String =_

  def getId = id

  def setParallelism(parallelism:Int)= this.parallelism =parallelism

  def getName = name

  def getOperator = operator

}

object Transformation{

  protected var idCounter = 0

  def getNewNodeId: Int = {
    idCounter += 1
    idCounter
  }
}
