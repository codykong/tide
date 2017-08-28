package com.xten.tide.runtime.api.environment

import com.xten.tide.api.common.ExecutionConfig
import com.xten.tide.runtime.api.datastream.DataStreamSource
import com.xten.tide.runtime.api.functions.source.{ParallelSourceFunction, SourceFunction}
import com.xten.tide.runtime.api.graph.{StreamGraph, StreamGraphGenerator}
import com.xten.tide.runtime.api.operators.SourceOperator
import com.xten.tide.runtime.api.transformations.Transformation
import com.xten.tide.runtime.util.Preconditions

import scala.collection.mutable.ArrayBuffer

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/28 
  */
object ExecutionEnvironment{

  final val DEFAULT_JOB_NAME =  "TideJob"

  var contextEnvironmentFactory : ExecutionEnvironmentFactory = null

  /**
    * 获取执行环境，目前默认为获取本地环境
    * @return
    */
  def getExecutionEnvironment(): ExecutionEnvironment ={

    if (contextEnvironmentFactory !=null){
      contextEnvironmentFactory.createExecutionEnvironment()
    }else {
      LocalEnvironment.getLocalEnvironment()
    }

  }
}

abstract class ExecutionEnvironment {

  val config : ExecutionConfig = new ExecutionConfig

  protected final val transformations : ArrayBuffer[Transformation] = ArrayBuffer.empty

  def addSource(function:SourceFunction):DataStreamSource={
    this.addSource(function,function.name)
  }

  def addSource(function:SourceFunction,sourceName:String):DataStreamSource={

    val sourceOperator = new SourceOperator(function)
    val isParallel = function.isInstanceOf[ParallelSourceFunction]

    DataStreamSource(isParallel,this,sourceOperator,sourceName)

//    DataStreamSource(isParallel,this,)
  }

  def addOperator(transformation: Transformation) = {
    Preconditions.checkNotNull(transformation, "transformation must not be null.")
    this.transformations += transformation
  }

  def getParallelism: Int = config.getParallelism


  @throws[Exception]
  def execute(jobName: String)

  def getStreamGraph(): StreamGraph ={

    if (transformations.size <=0){
      throw new IllegalStateException("No operators defined in streaming topology. Cannot execute.")
    }

    StreamGraphGenerator.generate(this,getParallelism,this.transformations.toList)


  }




}
