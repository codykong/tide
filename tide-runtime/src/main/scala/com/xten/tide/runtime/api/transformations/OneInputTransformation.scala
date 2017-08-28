package com.xten.tide.runtime.api.transformations

import com.xten.tide.runtime.api.operators.Operator

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/25 
  */
class OneInputTransformation(name : String, parallelism :Int,operator :Operator,private var input : Transformation)extends Transformation(name,parallelism,operator){


  def getInput = this.input
}

class MultiInputTransformation(name : String, parallelism :Int,operator :Operator,private var input : Transformation)extends Transformation(name,parallelism,operator){


  def getInput = this.input
}

