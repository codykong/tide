package com.xten.tide.runtime.api.graph

import java.util.UUID

import com.xten.tide.runtime.api.environment.ExecutionEnvironment
import com.xten.tide.runtime.api.operators.Operator

import scala.collection.mutable.ListBuffer

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/25 
  */
class StreamNode(private val env: ExecutionEnvironment,
                 private val id: Int,
                 val name: String,
                 private val operator: Operator,
                 val parallelism: Int
                ) extends Serializable {
  val streamNodeId: String = UUID.randomUUID().toString
  val jobVertexClass: Class[_ <: Any] = operator.getFunction.getClass
  private var inNodes: ListBuffer[String] = ListBuffer.empty
  private var outNodes: ListBuffer[String] = ListBuffer.empty

  def addInNode(inNode: StreamNode): List[String] = {
    this.inNodes += inNode.streamNodeId
    this.inNodes.toList
  }

  def addOutNode(outNode: StreamNode): List[String] = {
    this.outNodes += outNode.streamNodeId
    this.outNodes.toList
  }

  def getOutNodes(): List[String] = {
    outNodes.toList
  }

  def getInNodes(): List[String] = {
    inNodes.toList
  }

  def getOperator(): Operator = {
    operator
  }

  def getId = this.id


  def getExecutionNode(): ExecutionNode = {
    new ExecutionNode(this.id, this.name, this.parallelism, this.jobVertexClass.getName, this.streamNodeId
      , this.inNodes.toList, this.outNodes.toList)
  }


}

