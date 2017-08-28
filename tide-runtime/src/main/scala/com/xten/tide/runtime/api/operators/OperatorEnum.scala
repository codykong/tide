package com.xten.tide.runtime.api.operators

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/27 
  */

object OperatorTypeEnum extends Enumeration {
  type OperatorTypeEnum = Value
  val Source = Value(0, "Source")
  val Map = Value(1, "Map")
  val Sink = Value(2, "Sink")
}
