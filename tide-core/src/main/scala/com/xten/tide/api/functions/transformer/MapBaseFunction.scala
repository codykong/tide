package com.xten.tide.api.functions.transformer

import com.xten.tide.api.functions.BaseFunction

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/4/27 
  */
trait MapBaseFunction[T,O] extends BaseFunction{

  /**
    * The mapping method. Takes an element from the input data set and transforms
    * it into exactly one element.
    *
    * @param value The input value.
    * @return The transformed value
    * @throws Exception This method may throw exceptions. Throwing an exception will cause the operation
    *                   to fail and may trigger recovery.
    */
//    def map(value :T) :O throws Exception


}
