package com.xten.tide.runtime.runtime.jobmanager

import com.xten.tide.runtime.runtime.messages.cluster.Task


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/3 
  */
class JobManagerContext(val appMasterPath : String,val tasks: List[Task]) {

}
