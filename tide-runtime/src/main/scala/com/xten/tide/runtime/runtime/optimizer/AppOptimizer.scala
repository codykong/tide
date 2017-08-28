package com.xten.tide.runtime.runtime.optimizer

import com.xten.tide.runtime.runtime.appmaster.JobGraph
import com.xten.tide.runtime.runtime.messages.app.AppUpAction

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/24 
  */
trait AppOptimizer {

  def appUp(appUpAction: AppUpAction) : List[JobGraph]

  def appDown()

  def appChange()

}
