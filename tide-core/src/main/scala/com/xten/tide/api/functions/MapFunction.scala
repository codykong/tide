package com.xten.tide.api.functions

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/27 
  * Time: 上午10:54 
  */
trait MapFunction[T,O] extends Function{

  /**
    * The mapping method. Takes an element from the input data set and transforms
    * it into exactly one element.
    *
    * @param value The input value.
    * @return The transformed value
    * @throws Exception This method may throw exceptions. Throwing an exception will cause the operation
    *                   to fail and may trigger recovery.
    */
    def map(value :T) :O throws Exception


}
