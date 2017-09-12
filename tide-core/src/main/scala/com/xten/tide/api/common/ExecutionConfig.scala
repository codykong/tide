package com.xten.tide.api.common

import com.xten.tide.configuration.{ConfigConstants, Configuration}
import com.xten.tide.utils.Preconditions

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
  val PARALLELISM_DEFAULT: Int = - 1

  /**
    * The flag value indicating an unknown or unset parallelism. This value is
    * not a valid parallelism and indicates that the parallelism should remain
    * unchanged.
    */
  val PARALLELISM_UNKNOWN: Int = -2



  private var parallelism = PARALLELISM_DEFAULT
  /**
    * The program wide maximum parallelism used for operators which haven't specified a maximum
    * parallelism. The maximum parallelism specifies the upper limit for dynamic scaling and the
    * number of key groups used for partitioned state.
    */
  private var maxParallelism = -1

  def getParallelism: Int = parallelism

  def setParallelism(parallelism : Int) : ExecutionConfig = {

    Preconditions.checkArgument(parallelism != PARALLELISM_UNKNOWN ,"Cannot specify UNKNOWN_PARALLELISM.")
    Preconditions.checkArgument(parallelism >=1 || parallelism == PARALLELISM_DEFAULT ,
      "Parallelism must be at least one, or ExecutionConfig.PARALLELISM_DEFAULT " + "(use system default)."
    )

    Preconditions.checkArgument(maxParallelism == -1 || parallelism <= maxParallelism, "The specified parallelism must be smaller or equal to the maximum parallelism.")
    Preconditions.checkArgument(maxParallelism == -1 || parallelism != PARALLELISM_DEFAULT, "Default parallelism cannot be specified when maximum parallelism is specified")

    this.parallelism = parallelism

    this
  }


  def toConfiguration() : Configuration = {
    val configuration = Configuration.apply()
    configuration.add(ConfigConstants.EXECUTION_PARALLELISM_KEY,parallelism)

    configuration
  }



}
