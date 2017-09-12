package com.xten.tide.client.program

import com.xten.tide.runtime.api.graph.{ExecutionGraph, StreamGraph}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/28 
  */
class ClusterClient{
}

object ClusterClient {


  def getPlan(program : PackagedProgram ,parallelism : Int): ExecutionGraph = {

    Thread.currentThread().setContextClassLoader(program.getUserCodeClassLoader())

    var executionGraph : ExecutionGraph = null

    val env = new ClusterEnvironment

    if(parallelism >0) {
      env.setParallelism(parallelism)
    }

    val plan = env.getOptimizedPlan(program,parallelism)

    if (plan.isInstanceOf[StreamGraph]){
      executionGraph = plan.asInstanceOf[StreamGraph].toExecutionGraph()
    }

    if(executionGraph == null) {
      throw new RuntimeException("A valid job graph couldn't be generated for the jar.")
    }


    executionGraph.userJars = program.getAllLibraries()


    executionGraph
  }


}
