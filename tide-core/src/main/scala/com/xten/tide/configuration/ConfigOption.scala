package com.xten.tide.configuration

import com.google.common.base.Preconditions

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/3 
  */
class ConfigOption[T] (val key:String,val defaultValue :Option[T]) {


}

class OptionBuilder(private val key:String){

  def defaultValue[T](value : T): ConfigOption[T] ={
    Preconditions.checkNotNull(value)
    new ConfigOption(key,Some(value))
  }

  def noDefaultValue[T](): ConfigOption[T] ={
    new ConfigOption(key,None)
  }

  /**
    *
    * @param value 如果没有默认值，则传入None
    * @tparam T
    * @return
    */
  def defaultValue[T](value : Option[T]): ConfigOption[T] ={
    new ConfigOption(key,value)
  }
}

object ConfigOptions{

  def key(key:String):OptionBuilder={
    Preconditions.checkNotNull(key)

    return new OptionBuilder(key)
  }
}
