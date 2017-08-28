package com.xten.tide.client.program

import com.xten.tide.runtime.api.environment.{ExecutionEnvironment, ExecutionEnvironmentFactory}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/28 
  */
class ClusterEnvironment extends ExecutionEnvironment{
  @throws[Exception]
  override def execute(jobName: String): Unit = {

    val streamGraph = getStreamGraph()
    streamGraph.setJobName(jobName)

  }

  def getOptimizedPlan(program : PackagedProgram ,parallelism : Int) = {

    setAsContext()

    program.invokeInteractiveModeForExecution()

    unsetAsContext()

  }

  private def setAsContext() = {
    val factory  = new ExecutionEnvironmentFactory {
      override def createExecutionEnvironment() : ExecutionEnvironment = {
        ClusterEnvironment.this
      }
    }
    ExecutionEnvironment.contextEnvironmentFactory = factory

  }

  private def unsetAsContext() = {
    ExecutionEnvironment.contextEnvironmentFactory = null
  }
}
