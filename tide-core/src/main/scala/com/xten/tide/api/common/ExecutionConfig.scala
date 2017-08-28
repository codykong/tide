package com.xten.tide.api.common

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/16 
  */
class ExecutionConfig extends Serializable{
  /**
    * The flag value indicating use of the default parallelism. This value can
    * be used to reset the parallelism back to the default state.
    */
  val PARALLELISM_DEFAULT: Int = 1

  private var parallelism = PARALLELISM_DEFAULT
  /**
    * The program wide maximum parallelism used for operators which haven't specified a maximum
    * parallelism. The maximum parallelism specifies the upper limit for dynamic scaling and the
    * number of key groups used for partitioned state.
    */
  private var maxParallelism = -1

  def getParallelism: Int = parallelism



}
