package com.xten.tide.client.program

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/24 
  */
class ProgramInvocationException(message: String, cause: Throwable) extends Exception(message, cause) {
  def this(msg: String) = this(msg, null)
}
