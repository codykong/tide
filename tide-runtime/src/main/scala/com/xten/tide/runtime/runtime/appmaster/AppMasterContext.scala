package com.xten.tide.runtime.runtime.appmaster

import com.xten.tide.configuration.Configuration

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/21 
  */
class AppMasterContext(config : Configuration,val resourceManger : String) {

}

object AppMasterContext{


  def apply(config: Configuration, resourceManger: String): AppMasterContext =
    new AppMasterContext(config,resourceManger)

  def apply(resourceManger : String): AppMasterContext = new AppMasterContext(Configuration.apply(),resourceManger)
}
