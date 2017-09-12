package com.xten.tide.runtime.api.graph

import java.net.URL
import java.nio.file.Path
import javax.print.attribute.standard.JobName

import com.xten.tide.configuration.Configuration

/**
  * Description: 
  * User: kongqingyu
  * Date: 2017/9/6 
  */
case class ExecutionGraph(appId:String,
                          appName: String ,
                     executionConfig : Configuration,
                     executionNodes : List[ExecutionNode]
                    ) {
  var userJars : List[URL] = _


  def getUserJars() = userJars


}

case class ExecutionNode(id :Int,
                         name :String,
                         parallelism : Int,
                         jobVertexClass : String,
                         streamNodeId : String,
                         inNodes : List[String],
                         outNodes : List[String]
                        )