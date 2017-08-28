package com.xten.tide.runtime.util


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/18 
  */
object Preconditions {

  /**
    * Ensures that the given object reference is not null.
    * @param reference
    * @param errorMessage
    * @tparam T
    * @return
    */
  def checkNotNull[T](reference: T, errorMessage: String): T = {
    if (reference == null) throw new NullPointerException(errorMessage)
    reference
  }

  def checkArgument(condition: Boolean, errorMessage: Any) {
    if (!condition) throw new IllegalArgumentException(String.valueOf(errorMessage))
  }

  def checkArgument(condition: Boolean) {
    if (!condition) throw new IllegalArgumentException()
  }

}
