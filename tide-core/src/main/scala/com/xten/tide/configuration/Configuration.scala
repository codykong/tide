package com.xten.tide.configuration


import akka.ConfigurationException
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.collection.{JavaConverters, mutable}

/**
  * Created with IntelliJ IDEA.
  * User: kongqingyu
  * Date: 2017/5/5
  */
class Configuration(private final val configData : mutable.HashMap[String,Any]) extends Serializable{

  private final val LOG = LoggerFactory.getLogger(Configuration.getClass)

  def this() ={
    this(new mutable.HashMap)
  }

  def containsKey(key : String): Boolean ={
    configData.synchronized{
      return configData.contains(key)
    }
  }


  private def getValueOrDefaultFromOption[T](config: ConfigOption[T]): Any ={

    val o = getRawValue(config.key)
    if (o.isEmpty && config.defaultValue.isEmpty) {
      throw new ConfigurationException(s"Configuration has no value for this key:${config.key}")
    }else if(!o.isEmpty) o.get else config.defaultValue.get

  }

  def getBoolean(config: ConfigOption[Boolean]) :Boolean ={
    val value = getValueOrDefaultFromOption(config)
    convertToBoolean(value,config.defaultValue)
  }

  def getBoolean(key:String,defaultValue:Option[Boolean]) :Boolean ={
    this.getBoolean(ConfigOptions.key(key).defaultValue(defaultValue))
  }

  def convertToBoolean(o:Any,defaultValue:Option[Boolean]) :Boolean = {
    if (o.isInstanceOf[Boolean]){
      return o.asInstanceOf[Boolean]
    }else{
      try {
        return o.toString.toBoolean
      } catch {
        case e : Exception => {
          LOG.warn(s"Configuration cannot evaluate value ${o} as an Boolean number")
        }
      }
    }

    if (!defaultValue.isEmpty){
      return defaultValue.get
    }else{
      throw new ConfigurationException(s"Configuration cannot evaluate value ${o} as an Boolean number")
    }
  }

  def getDouble(config: ConfigOption[Double]) :Double ={
    val value = getValueOrDefaultFromOption(config)
    convertToDouble(value,config.defaultValue)
  }

  def getDouble(key:String,defaultValue : Option[Double]) :Double ={
    this.getDouble(ConfigOptions.key(key).defaultValue(defaultValue))
  }

  def convertToDouble(o:Any,defaultValue : Option[Double]) :Double ={

    if (o.isInstanceOf[Double]){
      return o.asInstanceOf[Double]
    }else if (o.isInstanceOf[Float]){
      return o.asInstanceOf[Float].toDouble
    }else{
      try {
        return o.toString.toDouble
      } catch {
        case e : NumberFormatException => {
          LOG.warn("Configuration cannot evaluate value {} as an Double number", o)
        }
      }
    }
    if (!defaultValue.isEmpty){
      return defaultValue.get
    }else{
      throw new ConfigurationException(s"Configuration cannot evaluate value ${o} as an Boolean number")
    }
  }


  def getInt(config: ConfigOption[Int]) :Int ={
    val value = getValueOrDefaultFromOption(config)
    convertToInt(value,config.defaultValue)
  }

  def getInt(key:String,defaultValue : Option[Int]) :Int ={
    this.getInt(ConfigOptions.key(key).defaultValue(defaultValue))
  }

  def convertToInt(o:Any,defaultValue : Option[Int]) :Int ={

    if (o.isInstanceOf[Int]){
      return o.asInstanceOf[Int]
    }else if (o.isInstanceOf[Long]){
      val value : Long= o.asInstanceOf[Long]
      if (value <= Int.MaxValue && value >= Int.MinValue){
        return value.asInstanceOf[Int]
      }
    }else{
      try {
        return o.toString.toInt
      } catch {
        case e : NumberFormatException => {
          LOG.warn("Configuration cannot evaluate value {} as an Int number", o)
        }
      }
    }

    if (!defaultValue.isEmpty){
      return defaultValue.get
    }else{
      throw new ConfigurationException(s"Configuration cannot evaluate value ${o} as an Int number")
    }
  }

  def getStringList(key:String) : List[String] = {
    val defaultValue = Some(List.empty[String])
    val value = getValueOrDefaultFromOption(ConfigOptions.key(key).defaultValue(defaultValue))

    convertToList(value)
  }

  def convertToList(o:Any):List[String] = {
    var res = mutable.Buffer.empty[String]
    if (o.isInstanceOf[java.util.List[String]]){
      res = JavaConverters.asScalaBuffer(o.asInstanceOf[java.util.List[String]])
    }
    res.toList

  }

  def getString(config: ConfigOption[String]) :String ={
    val value = getValueOrDefaultFromOption(config)
    value.toString
  }

  def getString(key:String,defaultValue : Option[String]) :String ={
    this.getString(ConfigOptions.key(key).defaultValue(defaultValue))
  }

  def setInt(key:String,value :Int): Unit ={
    setValueInternal(key,value)
  }

  def setLong(key:String,value :Long): Unit ={
    setValueInternal(key,value)
  }

  def setString(key:String,value :String): Unit ={
    setValueInternal(key,value)
  }

  def setValueInternal[T](key :String ,value :T): Unit ={
    if (key == null) throw new NullPointerException("Key must not be null.")
    if (value == null) throw new NullPointerException("Value must not be null.")
    configData.synchronized {
      this.configData.put(key, value)
    }
  }


  def getOption[T](config: ConfigOption[T]) :Option[T] ={
    getOption(config.key,config.defaultValue)
  }

  def getOption[T](key:String,defaultValue : Option[T]) :Option[T] ={
    val o = getRawValue(key)

    if (o.isEmpty ){
      return defaultValue
    }

    return Some(o.get.asInstanceOf[T])
  }



  private def getRawValue(key :String):Option[Any]={
    if (key == null){
      throw new NullPointerException("Key must not be null.")
    }

    configData.synchronized {
       this.configData.get(key)
    }
  }

  def getConfigData : Map[String,Any] = configData.toMap


  override def clone(): Configuration ={
    val config = new Configuration()
    config.addAll(this)

    config
  }

  def addAll(other : Configuration) : Unit = {
    synchronized(this.configData){
      synchronized(other.configData){
        this.configData.++=(other.configData)
        ""
      }
      ""
    }
  }


  def add(key : String ,value : Any): Unit = {
    this.configData.put(key,value)
  }
}

object Configuration{


  def apply(config : Config) = {
    val map = JavaConverters.asScalaSet(config.entrySet()).map( v => v.getKey -> v.getValue.unwrapped().asInstanceOf[Any]).toMap
    new Configuration(new mutable.HashMap[String,Any]().++=(map))
  }

  def apply(map : mutable.Map[String,Any]): Configuration = {
    new Configuration(new mutable.HashMap[String,Any]().++=(map))
  }

  def apply() = new Configuration(new mutable.HashMap[String,Any]())

}


