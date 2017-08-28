package com.xten.tide.runtime.api.graph

import java.util.UUID

import com.xten.tide.runtime.api.environment.ExecutionEnvironment
import com.xten.tide.runtime.api.operators.Operator
import com.xten.tide.runtime.runtime.jobgraph.Task

import scala.collection.mutable.ListBuffer

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/25 
  */
class StreamNode(private val env : ExecutionEnvironment,
                 private val id :Int,
                 val name :String,
                 private val operator :Operator,
                 val parallelism : Int
                ) extends Serializable{
  private var inNodes : ListBuffer[String] = ListBuffer.empty
  private var outNodes : ListBuffer[String] = ListBuffer.empty
  val streamNodeId : String = UUID.randomUUID().toString
  val jobVertexClass : Class[_ <: Any] = operator.getFunction.getClass

  private var tasks: ListBuffer[Task] = ListBuffer.empty

  def addTask(task : Task) = {
    tasks += task
  }

  def getTasks() : List[Task] = tasks.toList

  def addInNode(inNode : StreamNode): List[String] ={
    this.inNodes += inNode.streamNodeId
    this.inNodes.toList
  }

  def addOutNode(outNode : StreamNode) : List[String] = {
    this.outNodes += outNode.streamNodeId
    this.outNodes.toList
  }

  def getOutNodes():List[String] = {
    outNodes.toList
  }

  def getInNodes():List[String] = {
    inNodes.toList
  }

  def getOperator() : Operator = {
    operator
  }

  def getId = this.id




}

