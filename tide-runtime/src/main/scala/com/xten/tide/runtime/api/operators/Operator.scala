package com.xten.tide.runtime.api.operators

import com.xten.tide.api.functions.BaseFunction

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/28 
  */
trait Operator extends Serializable{

  /**
    * 获取对应的Function
    * @return
    */
  def getFunction:BaseFunction

}
