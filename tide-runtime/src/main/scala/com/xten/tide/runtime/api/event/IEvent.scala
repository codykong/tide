package com.xten.tide.runtime.api.event


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/23 
  */
trait IEvent {

  def isEmpty() : Boolean

  def isDefined() : Boolean

}
