package com.xten.tide.runtime.api.transformations

import com.xten.tide.runtime.api.operators.{Operator}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/18 
  */
class SourceTransformation(name : String, parallelism :Int,operator :Operator) extends Transformation(name,parallelism,operator){


}

