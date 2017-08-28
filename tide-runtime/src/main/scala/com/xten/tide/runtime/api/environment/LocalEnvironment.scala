package com.xten.tide.runtime.api.environment

import com.typesafe.config.ConfigFactory
import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.api.functions.source.{ParallelSourceFunction, SourceFunction}
import com.xten.tide.runtime.api.graph.StreamGraphGenerator
import com.xten.tide.runtime.api.operators.SourceOperator
import com.xten.tide.runtime.runtime.jobgraph.JobGraphGenerator
import com.xten.tide.runtime.runtime.minicluster.LocalMiniCluster


object LocalEnvironment{

  def getLocalEnvironment(): LocalEnvironment ={

    new LocalEnvironment
  }

}
/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/27 
  */
class LocalEnvironment() extends ExecutionEnvironment with Serializable{


  private val DEFAULT_LOCAL_PARALLELISM = Runtime.getRuntime.availableProcessors()

  var parallelism : Int =DEFAULT_LOCAL_PARALLELISM
//  def this(){
//    this(DEFAULT_LOCAL_PARALLELISM)
//  }

//  var sourceOperator :SourceOperator =null



  @throws[Exception]
  override def execute(jobName: String): Unit = {

    val streamGraph = getStreamGraph()
    streamGraph.setJobName(jobName)

//    val jobGraph = JobGraphGenerator.createJobGraph(streamGraph)

    val config = Configuration.apply()
    val miniCluster = LocalMiniCluster.apply(config)
    miniCluster.start()
    miniCluster.submitApp(streamGraph)


  }
}
