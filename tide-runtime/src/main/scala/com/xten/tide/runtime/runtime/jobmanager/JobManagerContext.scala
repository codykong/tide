package com.xten.tide.runtime.runtime.jobmanager


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/3 
  */
class JobManagerContext(val appMasterPath : String) {

}

object JobManagerContext{
  def apply(appMasterPath: String): JobManagerContext = new JobManagerContext(appMasterPath)
}
