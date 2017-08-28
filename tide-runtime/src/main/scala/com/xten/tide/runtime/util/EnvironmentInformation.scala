package com.xten.tide.runtime.util

import java.lang.management.ManagementFactory

import com.xten.tide.configuration.Configuration
import org.apache.hadoop.security.UserGroupInformation
import org.apache.hadoop.util.VersionInfo
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/20 
  */
object EnvironmentInformation {

  private val LOG = LoggerFactory.getLogger(EnvironmentInformation.getClass)

  val UNKNOWN = "<unknown>"

  def logEnvironmentInfo(log:Logger , componentName : String ,commandLineArgs : Array[String]) = {

    val user = getUserRunning
    val jvmVersion = getJvmVersion
    val maxHeapMegabytes = getMaxJvmHeapMemory
    val javaHome = System.getenv("JAVA_HOME")
    val options = getJvmStartupOptionsArray

    log.info("--------------------------------------------------------------------------------")
//    log.info(" Starting " + componentName + " (Version: " + version + ", " + "Rev:" + rev.commitId + ", " + "Date:" + rev.commitDate + ")")
    log.info(" Current user: " + user)
    log.info(" JVM: " + jvmVersion)
    log.info(" Maximum heap size: " + maxHeapMegabytes + " MiBytes")
    log.info(" JAVA_HOME: " + (if (javaHome == null) "(not set)"
    else javaHome))
    log.info(" Hadoop version: " + VersionInfo.getVersion)
    if (options.length == 0) log.info(" JVM Options: (none)")
    else {
      log.info(" JVM Options:")
      for (s <- options) {
        log.info("    " + s)
      }
    }
  }

  /**
    * Gets the name of the user that is running the JVM.
    *
    * @return The name of the user that is running the JVM.
    */
  def getUserRunning: String = {
    try
      return UserGroupInformation.getCurrentUser.getShortUserName

    catch {
      case e: LinkageError => {
        // hadoop classes are not in the classpath
        LOG.debug("Cannot determine user/group information using Hadoop utils. " + "Hadoop classes not loaded or compatible", e)
      }
      case t: Throwable => {
        // some other error occurred that we should log and make known
        LOG.warn("Error while accessing user/group information via Hadoop utils.", t)
      }
    }
    var user = System.getProperty("user.name")
    if (user == null) {
      user = UNKNOWN
      LOG.debug("Cannot determine user/group information for the current user.")
    }
    user
  }

  /**
    * Gets the version of the JVM in the form "VM_Name - Vendor  - Spec/Version".
    *
    * @return The JVM version.
    */
  def getJvmVersion: String ={

    try {
      val bean = ManagementFactory.getRuntimeMXBean
      bean.getVmName + " - " + bean.getVmVendor + " - " + bean.getSpecVersion + '/' + bean.getVmVersion

    }catch {
      case t: Throwable => {
        UNKNOWN
      }
    }
  }

  /**
    * The maximum JVM heap size, in bytes.
    *
    * <p>This method uses the <i>-Xmx</i> value of the JVM, if set. If not set, it returns (as
    * a heuristic) 1/4th of the physical memory size.
    *
    * @return The maximum JVM heap size, in bytes.
    */
  def getMaxJvmHeapMemory: Long = {
    val maxMemory = Runtime.getRuntime.maxMemory
    if (maxMemory != Long.MaxValue) {
      // we have the proper max memory
      maxMemory
    }
    else {
      // todo
      // max JVM heap size is not set - use the heuristic to use 1/4th of the physical memory
//      val physicalMemory = Hardware.getSizeOfPhysicalMemory
//      if (physicalMemory != -1) {
//        // got proper value for physical memory
//        physicalMemory / 4
//      }
//      else throw new RuntimeException("Could not determine the amount of free memory.\n" + "Please set the maximum memory for the JVM, e.g. -Xmx512M for 512 megabytes.")
      0
    }
  }

  /**
    * Gets the system parameters and environment parameters that were passed to the JVM on startup.
    *
    * @return The options passed to the JVM on startup.
    */
  def getJvmStartupOptionsArray: Array[String] = {
    try{
      val bean = ManagementFactory.getRuntimeMXBean
      val options = bean.getInputArguments
      options.toArray(new Array[String](options.size))

    }catch {
      case t: Throwable => {
        new Array[String](0)
      }
    }
  }

}
