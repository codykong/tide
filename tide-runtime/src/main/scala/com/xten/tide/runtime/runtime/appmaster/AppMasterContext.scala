package com.xten.tide.runtime.runtime.appmaster

import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.api.graph.ExecutionGraph

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/21 
  */
class AppMasterContext(config : Configuration,val resourceManger : String,val executionGraph: ExecutionGraph) {

}

object AppMasterContext{


  def apply(config: Configuration, resourceManger: String,executionGraph: ExecutionGraph): AppMasterContext =
    new AppMasterContext(config,resourceManger,executionGraph)

}
