package com.xten.tide.runtime.util

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/21 
  */
object Hardware {

  /**
    * Gets the number of CPU cores (hardware contexts) that the JVM has access to.
    *
    * @return The number of CPU cores.
    */
  def getNumberCPUCores(): Int = Runtime.getRuntime.availableProcessors()

}
