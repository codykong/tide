package com.xten.tide.client.program

import com.xten.tide.optimizer.plan.TidePlan
import com.xten.tide.runtime.api.environment.{ExecutionEnvironment, ExecutionEnvironmentFactory}
import org.slf4j.LoggerFactory

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/28 
  */
class ClusterEnvironment extends ExecutionEnvironment{

  private val LOG = LoggerFactory.getLogger(this.getClass)

  var tidePlan : TidePlan = _

  @throws[Exception]
  override def execute(jobName: String): Unit = {

    tidePlan = getStreamGraph()

    throw new ProgramAbortException()


  }

  def getOptimizedPlan(program : PackagedProgram ,parallelism : Int) : TidePlan = {

    setAsContext()

    try {
      program.invokeInteractiveModeForExecution()
    } catch {
      case e : ProgramInvocationException => throw e
      case t : Throwable => {
        if (tidePlan != null) {
          return tidePlan
        }else {
          throw new ProgramInvocationException("The program caused an error: ", t)
        }
      }
    }finally {
      unsetAsContext()
    }

    throw new ProgramInvocationException(
      "The program plan could not be fetched - the program aborted pre-maturely.")


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
