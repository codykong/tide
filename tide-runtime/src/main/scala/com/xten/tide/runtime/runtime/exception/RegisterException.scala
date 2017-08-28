package com.xten.tide.runtime.runtime.exception

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/1 
  */
class RegisterException(message :String) extends RuntimeException(message){

}

object RegisterException {
  def apply(): RegisterException = {
    new RegisterException("")
  }

  def apply(message: String): RegisterException =
    new RegisterException(message)
}
