package com.xten.tide.runtime.api.operators

import com.xten.tide.api.functions.BaseFunction

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/3 
  */
abstract class AbstractOperator(protected val userFunction:BaseFunction) extends Operator{

  private val serialVersionUID = 1L

  override def getFunction: BaseFunction = this.userFunction




}
