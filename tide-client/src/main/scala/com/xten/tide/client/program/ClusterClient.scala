package com.xten.tide.client.program

import com.xten.tide.runtime.api.environment.ExecutionEnvironment
import com.xten.tide.runtime.api.graph.StreamGraph

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/28 
  */
class ClusterClient{
}

object ClusterClient {


  def getPlan(program : PackagedProgram ,parallelism : Int): StreamGraph = {

    Thread.currentThread().setContextClassLoader(program.getUserCodeClassLoader())

    val env = new ClusterEnvironment

    env.getStreamGraph()
  }

}
