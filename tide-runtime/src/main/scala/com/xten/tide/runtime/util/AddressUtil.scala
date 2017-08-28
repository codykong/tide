package com.xten.tide.runtime.util

import java.io.IOException
import java.net.{InetAddress, Socket, UnknownHostException}

import org.slf4j.LoggerFactory

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/18 
  */
object AddressUtil {

  val LOG = LoggerFactory.getLogger(AddressUtil.getClass)
  /**
    * Returns the IP address string in textual presentation.
    * @return IP
    */
  def getHostIP: String = InetAddress.getLocalHost.getHostAddress

  /**
    * Gets the host name for this IP address.
    * @return hostName
    */
  def getHostName: String = InetAddress.getLocalHost.getHostName

  /**
    * 检查端口是否空闲
    * @param host
    * @param port
    * @return
    */
  def isIdlePort(host: String, port: Int) : Boolean = {
    // Assume port is available.
    var result = true
    try {
      new Socket(host, port).close()
      // Successful connection means the port is taken.
      result = false
    } catch {
      case e: IOException => {
        // Could not connect.
        LOG.info("Could not connect,port is available.Port is " + port)
      }
    }

    result

  }


}

