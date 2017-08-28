package com.xten.tide.runtime.api.graph

import java.util.UUID

import com.xten.tide.api.common.ExecutionConfig
import com.xten.tide.runtime.api.environment.ExecutionEnvironment
import com.xten.tide.runtime.api.transformations.{OneInputTransformation, SinkTransformation, SourceTransformation, Transformation}
import com.xten.tide.runtime.runtime.jobgraph.JobGraph
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/16 
  */
class StreamGraph(environment : ExecutionEnvironment,val parallelism :Int) extends Serializable{

  val jobId = UUID.randomUUID().toString

  private val LOG = LoggerFactory.getLogger(classOf[StreamGraph])

  private var jobName = ExecutionEnvironment.DEFAULT_JOB_NAME

  private val executionConfig : ExecutionConfig =  new ExecutionConfig

  private var sources : Set[Int] = Set.empty
  private var sinks : Set[Int] = Set.empty

  var streamNodes :Map[String,StreamNode] = Map.empty[String,StreamNode]

  def setSteamNodes (streamNodes :List[StreamNode]): Unit ={
    this.streamNodes = streamNodes.map(p => (p.streamNodeId,p)).toMap
  }





  def setJobName(jobName:String) = {
    this.jobName = jobName
  }

  def getJobName = this.jobName

  def getExecutionConfig = this.executionConfig




}

class StreamGraphGenerator(env : ExecutionEnvironment , defaultParallelism : Int){
  val streamGraph = new StreamGraph(env,defaultParallelism)
  var hasTransformed : mutable.HashMap[Int,StreamNode] = mutable.HashMap.empty

  def generate(transformations : List[Transformation]):StreamGraph={
    for (transformation <- transformations){
      this.transform(transformation)

    }
    streamGraph.setSteamNodes(hasTransformed.values.toList)
    streamGraph
  }

  private def transform(transformation :Transformation): StreamNode ={
    if (transformation.isInstanceOf[OneInputTransformation]){
      transformOneInputTransformation(transformation.asInstanceOf[OneInputTransformation])
    }else if (transformation.isInstanceOf[SourceTransformation]){
      transformSourceTransformation(transformation.asInstanceOf[SourceTransformation])
    }else if (transformation.isInstanceOf[SinkTransformation]){
      transformSinkTransformation(transformation.asInstanceOf[SinkTransformation])
    }else{
      null
    }

  }

  private def transformOneInputTransformation(transformation: OneInputTransformation) : StreamNode={

    var inputNode = hasTransformed.get(transformation.getInput.getId)
    if (inputNode.isEmpty){
      inputNode =  Some(this.transform(transformation.getInput))
    }

    val streamNode = new StreamNode(env,transformation.getId,transformation.getName,transformation.getOperator
      ,transformation.parallelism)

    streamNode.addInNode(inputNode.get)
    inputNode.get.addOutNode(streamNode)
    hasTransformed += (streamNode.getId -> streamNode)
    streamNode
  }

  private def transformSinkTransformation(transformation: SinkTransformation) : StreamNode={

    var inputNode = hasTransformed.get(transformation.getInput.getId)
    if (inputNode.isEmpty){
      inputNode =  Some(this.transform(transformation.getInput))
    }

    val streamNode = new StreamNode(env,transformation.getId,transformation.getName,transformation.getOperator
      ,transformation.parallelism)

    streamNode.addInNode(inputNode.get)
    inputNode.get.addOutNode(streamNode)
    hasTransformed += (streamNode.getId -> streamNode)

    streamNode
  }

  private def transformSourceTransformation(transformation: SourceTransformation) : StreamNode={


    val streamNode = new StreamNode(env,transformation.getId,transformation.getName,transformation.getOperator
      ,transformation.parallelism)
    hasTransformed += (streamNode.getId -> streamNode)
    //    if (hasTransformed)
    streamNode
  }



}





object StreamGraphGenerator{
  def generate(env : ExecutionEnvironment , defaultParallelism : Int ,transformations : List[Transformation]) :StreamGraph  = {
    val graphGenerator = new StreamGraphGenerator(env,defaultParallelism)
    graphGenerator.generate(transformations)
  }
}
