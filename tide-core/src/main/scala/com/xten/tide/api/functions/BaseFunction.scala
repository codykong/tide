package com.xten.tide.api.functions


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/27 
  */
trait BaseFunction extends Serializable{

  var name : String = this.getClass.getSimpleName


}
