package com.xten.tide.runtime.runtime.appmaster

import com.xten.tide.configuration.{ConfigConstants, Configuration}
import com.xten.tide.runtime.api.graph.{ExecutionGraph, ExecutionNode}
import com.xten.tide.runtime.runtime.messages.cluster.MemberStatus

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Description: 
  * User: kongqingyu
  * Date: 2017/9/7 
  */
class AppRuntime(val jobId:String,
                 val jobName: String ,
                 val executionConfig : Configuration,
                 val operatorRuntimes : mutable.HashMap[String,OperatorRuntime]
                ){

  private var parallelism = executionConfig.getInt(ConfigConstants.EXECUTION_PARALLELISM_KEY,None)

  /**
    * 获取并行度
    * @return
    */
  def getParallelism() : Int= parallelism




}

object AppRuntime{

  def apply(executionGraph: ExecutionGraph): AppRuntime = {

    val operatorRuntimes = mutable.HashMap.empty[String,OperatorRuntime]

    executionGraph.executionNodes.map(p => operatorRuntimes.put(p.streamNodeId,OperatorRuntime(p)))

    new AppRuntime(executionGraph.appId,executionGraph.appName,executionGraph.executionConfig,
      operatorRuntimes)
  }

}


class OperatorRuntime(executionNode: ExecutionNode){
  var memberStatus : MemberStatus = MemberStatus.Init
  var receiverMap = new mutable.HashMap[String,ListBuffer[String]]()

  private val taskRuntimes = ListBuffer.empty[TaskRuntime]

  def getParallelism() : Int= executionNode.parallelism

  def getOperatorId() : String = executionNode.streamNodeId

  def getOperatorName() : String = executionNode.name


  def getClassName() : String = executionNode.jobVertexClass

  def getOutOperatorIds : List[String] = executionNode.outNodes

  def getInOperatorIds : List[String] = executionNode.inNodes

  def addTask(taskRuntime: TaskRuntime) = taskRuntimes +=(taskRuntime)


  def getTaskRuntimes : List[TaskRuntime] = taskRuntimes.toList




}

object OperatorRuntime {
  def apply(executionNode: ExecutionNode): OperatorRuntime = new OperatorRuntime(executionNode)
}
