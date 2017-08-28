package com.xten.tide.runtime.runtime.optimizer

import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.runtime.appmaster.JobGraph
import com.xten.tide.runtime.runtime.jobgraph.Task
import com.xten.tide.runtime.runtime.messages.app.AppUpAction
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * 保证每个Task的组件数均匀
  * User: kongqingyu
  * Date: 2017/7/24 
  */
object BalanceAppOptimizer extends AppOptimizer{

  private val LOG = LoggerFactory.getLogger(BalanceAppOptimizer.getClass)

  override def appUp(appUpAction: AppUpAction): List[JobGraph] = {

    val taskNum = appUpAction.streamGraph.parallelism

    // 初始化每个task实例
    val jobGraphs = new mutable.HashMap[Int,JobGraph]
    for (i <- 0 until taskNum){
      jobGraphs.put(i,JobGraph(Configuration()))
    }

    var offset = 0
    appUpAction.streamGraph.streamNodes
      .flatMap(p => Task.transformTask(p._2))
      .foreach(p => {
        jobGraphs.get(offset % taskNum).get.addTask(p)
        appUpAction.streamGraph.streamNodes.get(p.streamNodeId).get.addTask(p)
        offset += 1
      })

    jobGraphs.values.toList

  }

  override def appDown(): Unit = ???

  override def appChange(): Unit = ???
}




