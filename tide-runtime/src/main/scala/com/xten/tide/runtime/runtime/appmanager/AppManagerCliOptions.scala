package com.xten.tide.runtime.runtime.appmanager

import com.xten.tide.runtime.runtime.appmanager.AppManagerMode.AppManagerMode
import org.slf4j.LoggerFactory

import scala.beans.BeanProperty

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/20 
  */
class AppManagerCliOptions {

  private val LOG = LoggerFactory.getLogger(classOf[AppManagerCliOptions])

  @BeanProperty
  var configDir : String =_
  @BeanProperty
  var host : String = _
  @BeanProperty
  var webUIPort = -1

  private var appManagerMode : AppManagerMode = _


  def setAppManagerMode(modeName : String) = {
    if (modeName.equalsIgnoreCase(AppManagerMode.CLUSTER.toString)) {
      this.appManagerMode = AppManagerMode.CLUSTER
      println(appManagerMode)
    } else if (modeName.equalsIgnoreCase(AppManagerMode.LOCAL.toString)) {
      this.appManagerMode = AppManagerMode.LOCAL
    }else {
      throw new IllegalArgumentException("Unknown execution mode. Execution mode must be one of 'cluster' or 'local'.")
    }
  }

  def getAppManagerMode() : AppManagerMode = {
    appManagerMode
  }



}


object AppManagerMode extends Enumeration {


  type AppManagerMode = Value
  /**
    * Causes the ResourceManager to operate in single user mode and
    * start a local embedded TaskManager.
    */
  val LOCAL = Value("local")
  /**
    * Starts the ResourceManager in the regular mode where it waits for external TaskManagers
    * to connect.
    */
  val CLUSTER = Value("cluster")

}
