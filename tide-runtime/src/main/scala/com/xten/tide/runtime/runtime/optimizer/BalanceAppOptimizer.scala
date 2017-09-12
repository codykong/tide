package com.xten.tide.runtime.runtime.optimizer

import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.runtime.appmaster._
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

  override def appUp(appRuntime: AppRuntime): List[JobRuntime] = {

    val jobNum = appRuntime.getParallelism()

    // 初始化每个task实例
    val jobRuntimeMap = new mutable.HashMap[Int,JobRuntime]
    for (i <- 0 until jobNum){
      jobRuntimeMap.put(i,JobRuntime(appRuntime.executionConfig))
    }

    var offset = 0

    appRuntime.operatorRuntimes.flatMap(p => TaskRuntime.transformTask(p._2)).foreach(p => {
      jobRuntimeMap.get(offset % jobNum).get.addTask(p)
      appRuntime.operatorRuntimes.get(p.operatorId).get.addTask(p)
      offset += 1
    })

    jobRuntimeMap.values.toList

  }

  override def appDown(): Unit = ???

  override def appChange(): Unit = ???
}




