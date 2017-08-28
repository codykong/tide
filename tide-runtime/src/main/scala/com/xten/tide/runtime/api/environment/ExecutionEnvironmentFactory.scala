package com.xten.tide.runtime.api.environment

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/28 
  */
trait ExecutionEnvironmentFactory {

  def createExecutionEnvironment() : ExecutionEnvironment

}
