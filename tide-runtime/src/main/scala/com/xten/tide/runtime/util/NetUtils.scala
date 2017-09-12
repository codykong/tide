package com.xten.tide.runtime.util

import java.io.IOException
import java.net.{InetAddress, Socket}
import java.util

import com.xten.tide.configuration.IllegalConfigurationException
import com.xten.tide.utils.Preconditions
import org.slf4j.LoggerFactory
import sun.net.util.IPAddressUtil

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/21 
  */
object NetUtils {

  private val LOG =LoggerFactory.getLogger(NetUtils.getClass)
  // ------------------------------------------------------------------------
  //  Lookup of to free ports
  // ------------------------------------------------------------------------
  // ------------------------------------------------------------------------
  //  Encoding of IP addresses for URLs
  // ------------------------------------------------------------------------
  def unresolvedHostAndPortToNormalizedString(host: String, port: Int): String = {
    Preconditions.checkArgument(port >= 0 && port < 65536, "Port is not within the valid range,")
    unresolvedHostToNormalizedString(host) + ":" + port
  }

  /**
    * Returns an address in a normalized format for Akka.
    * When an IPv6 address is specified, it normalizes the IPv6 address to avoid
    * complications with the exact URL match policy of Akka.
    *
    * @param host The hostname, IPv4 or IPv6 address
    * @return host which will be normalized if it is an IPv6 address
    */
  def unresolvedHostToNormalizedString(host: String): String = {
    var normalizedHost = host
    // Return loopback interface address if host is null
    // This represents the behavior of {@code InetAddress.getByName } and RFC 3330
    if (host == null) {
      normalizedHost = InetAddress.getLoopbackAddress.getHostAddress
    }else {
      normalizedHost = host.trim.toLowerCase
    }
    // normalize and valid address
    if (IPAddressUtil.isIPv6LiteralAddress(host)) {
      val ipV6Address = IPAddressUtil.textToNumericFormatV6(host)
      normalizedHost = getIPv6UrlRepresentation(ipV6Address)
    }
    else if (!IPAddressUtil.isIPv4LiteralAddress(host)) {
      try{

        // We don't allow these in hostnames
        Preconditions.checkArgument(!normalizedHost.startsWith("."))
        Preconditions.checkArgument(!normalizedHost.endsWith("."))
        Preconditions.checkArgument(!normalizedHost.contains(":"))

      }catch {
        case e: Exception => {
          throw new IllegalConfigurationException("The configured hostname is not valid", e)
        }
      }
    }
    host
  }

  /**
    * Creates a compressed URL style representation of an Inet6Address.
    *
    * <p>This method copies and adopts code from Google's Guava library.
    * We re-implement this here in order to reduce dependency on Guava.
    * The Guava library has frequently caused dependency conflicts in the past.
    */
  private def getIPv6UrlRepresentation(addressBytes: Array[Byte]) : String = {
    // first, convert bytes to 16 bit chunks
    val hextets = new Array[Int](8)
    for (i <- 0 until(hextets.length)) {
      hextets(i) = (addressBytes(2 * i) & 0xFF) << 8 | (addressBytes(2 * i + 1) & 0xFF)
    }

    // now, find the sequence of zeros that should be compressed
    var bestRunStart = -1
    var bestRunLength = -1
    var runStart = -1

    for(i <- 0 to hextets.length) {
      if (i < hextets.length && hextets(i) == 0) {
        if (runStart < 0) {
          runStart = i
        }else if (runStart >= 0) {
          val runLength = i - runStart
          if (runLength > bestRunLength) {
            bestRunStart = runStart
            bestRunLength = runLength
          }
          runStart = -1
        }
      }
    }
    if (bestRunLength >= 2) {
      util.Arrays.fill(hextets, bestRunStart, bestRunStart + bestRunLength, -1)
    }
    // convert into text form
    val buf = new StringBuilder(40)
    buf.append('[')
    var lastWasNumber = false
    var i = 0
    while (i < hextets.length) {
      {
        val thisIsNumber = hextets(i) >= 0
        if (thisIsNumber) {
          if (lastWasNumber) buf.append(':')
          buf.append(Integer.toHexString(hextets(i)))
        }
        else if (i == 0 || lastWasNumber) buf.append("::")
        lastWasNumber = thisIsNumber
      }
      {
        i += 1; i - 1
      }
    }
    buf.append(']')
    buf.toString
  }


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
