package com.xten.tide.runtime.runtime.akka

import akka.util.Timeout
import java.util.concurrent.TimeUnit
import scala.concurrent.duration._

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/23 
  */
object TimeoutConstant {

  val SYSTEM_MSG_DURATION = FiniteDuration.apply(20, TimeUnit.SECONDS)
  val SYSTEM_MSG_TIMEOUT = Timeout(20,TimeUnit.SECONDS)

  val TIMEOUT_5_SECONDS = Timeout(5,TimeUnit.SECONDS)
  val TIMEOUT_2_SECONDS = Timeout(2,TimeUnit.SECONDS)

  val DURATION_0_SECONDS = FiniteDuration.apply(0, TimeUnit.SECONDS)





}
