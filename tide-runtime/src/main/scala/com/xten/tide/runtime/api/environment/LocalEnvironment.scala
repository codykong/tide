package com.xten.tide.runtime.api.environment

import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.runtime.minicluster.LocalMiniCluster


object LocalEnvironment {

  def getLocalEnvironment(): LocalEnvironment = {

    new LocalEnvironment
  }

}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/27 
  */
class LocalEnvironment() extends ExecutionEnvironment with Serializable {


  private val DEFAULT_LOCAL_PARALLELISM = Runtime.getRuntime.availableProcessors()

  var parallelism: Int = DEFAULT_LOCAL_PARALLELISM
  //  def this(){
  //    this(DEFAULT_LOCAL_PARALLELISM)
  //  }

  //  var sourceOperator :SourceOperator =null


  @throws[Exception]
  override def execute(jobName: String): Unit = {

    val streamGraph = getStreamGraph()
    streamGraph.setJobName(jobName)

    val executionGraph = streamGraph.toExecutionGraph()

    //    val jobGraph = JobGraphGenerator.createJobGraph(streamGraph)

    val config = Configuration.apply()
    val miniCluster = LocalMiniCluster.apply(config)
    miniCluster.start(executionGraph)
    miniCluster.submitApp(executionGraph)


  }
}
