package com.xten.tide.runtime.runtime.resourcemanager

import com.xten.tide.configuration.Configuration

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/21 
  */
class ResourceContext(config : Configuration) {

  private var appManagerPath : String = _
}

object ResourceContext{
  def apply(config: Configuration,appManagerPath : String): ResourceContext = {
    val context = new ResourceContext(config)
    context.appManagerPath = appManagerPath
    context
  }


  def apply(config: Configuration): ResourceContext = new ResourceContext(config)


  def apply(): ResourceContext = new ResourceContext(Configuration.apply())
}
