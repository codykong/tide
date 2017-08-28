package com.xten.tide.runtime.runtime.nodemanager

import com.xten.tide.configuration.Configuration

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/21
  */
class NodeContext(val config : Configuration) {

}

object NodeContext{
  def apply(config: Configuration): NodeContext = new NodeContext(config)


  def apply(): NodeContext = new NodeContext(Configuration.apply())
}
