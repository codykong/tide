package com.xten.tide.runtime.configuration

import com.typesafe.config.ConfigFactory
import com.xten.tide.configuration.Configuration
import org.junit.Test

import scala.collection.JavaConverters

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/12 
  */
class ConfigurationTest {

  @Test
  def testGetConfigs() :Unit = {
    val config = ConfigFactory.load("akka_remote.conf")

    val conf = Configuration.apply(config)

    println(conf)
  }

  @Test
  def testOption() ={
    val a = None

    val b = a.get
    println(b)
  }

}
