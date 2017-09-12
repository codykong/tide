package com.xten.tide.runtime.api.datastream

import com.xten.tide.api.functions.transformer.MapBaseFunction
import com.xten.tide.runtime.api.environment.ExecutionEnvironment
import com.xten.tide.runtime.api.functions.MapFunction
import com.xten.tide.runtime.api.functions.sink.{PrintSinkFunction, SinkFunction}
import com.xten.tide.runtime.api.operators.{AbstractOperator, MapOperator, Operator, SinkOperator}
import com.xten.tide.runtime.api.transformations.{OneInputTransformation, SinkTransformation, Transformation}
import com.xten.tide.utils.Preconditions

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/18 
  */
class DataStream(protected val environment:ExecutionEnvironment,val transformation:Transformation) {

  Preconditions.checkNotNull(environment,"Execution Environment must not be null.")
  Preconditions.checkNotNull(transformation, "Stream Transformation must not be null.")

  def name(name :String ) : DataStream = {
    transformation.name = name
    this
  }

  def map(function: MapFunction) : DataStream ={
    val mapOperator = new MapOperator(function)
    transform(function.name,mapOperator)
  }

  def print():DataStreamSink = {

    val printFunction = new PrintSinkFunction()
    addSink(printFunction)
  }


  def transform(operatorName: String, operator: Operator): DataStream ={

    val resultTransformation = new OneInputTransformation(operatorName,environment.getParallelism,operator,transformation)
    environment.addOperator(resultTransformation)
    val resultStream = new DataStream(environment,resultTransformation)


    resultStream

  }


  def addSink(sinkFunction: SinkFunction) :DataStreamSink = {

    val sinkOperator = new SinkOperator(sinkFunction)
    val sink = new DataStreamSink(sinkOperator,this)
    environment.addOperator(sink.transformation)

    sink
  }

  def getExecutionEnvironment() = environment

}
