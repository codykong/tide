package com.xten.tide.runtime.runtime.optimizer

import com.xten.tide.runtime.runtime.appmaster.{AppRuntime, JobRuntime}
import com.xten.tide.runtime.runtime.messages.app.AppUpAction

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/24 
  */
trait AppOptimizer {

  def appUp(appRuntime: AppRuntime): List[JobRuntime]

  def appDown()

  def appChange()

}
