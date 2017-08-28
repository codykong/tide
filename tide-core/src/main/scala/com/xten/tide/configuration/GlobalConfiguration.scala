package com.xten.tide.configuration

import java.io._

import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml

import scala.collection.JavaConverters

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/20 
  */
object GlobalConfiguration {
  private val LOG = LoggerFactory.getLogger(GlobalConfiguration.getClass)
  val TIDE_CONF_FILENAME = "tide-conf.yaml"

  /**
    * Loads the configuration files from the specified directory.
    * <p>
    * YAML files are supported as configuration files.
    *
    * @param configDir
    * the directory which contains the configuration files
    */
  def loadConfiguration(configDir: String): Configuration = {
    if (configDir == null) {
      throw new IllegalArgumentException("Given configuration directory is null, cannot load configuration")
    }

    val confDirFile = new File(configDir)
    if (!confDirFile.exists) {
      throw IllegalConfigurationException("The given configuration directory name '" + configDir + "' (" + confDirFile.getAbsolutePath + ") does not describe an existing directory.")
    }

    // get Tide yaml configuration file
    val yamlConfigFile = new File(confDirFile, TIDE_CONF_FILENAME)
    if (!yamlConfigFile.exists) {
      throw IllegalConfigurationException("The Tide config file '" + yamlConfigFile + "' (" + confDirFile.getAbsolutePath + ") does not exist.")
    }

    val conf = loadYAMLResource(yamlConfigFile)
//    if (dynamicProperties != null) conf.addAll(dynamicProperties)
    conf
  }


  private def loadYAMLResource(file : File) : Configuration = {
    val yaml = new Yaml()

    val javaPropertyMap = yaml.load(new FileInputStream(file)).asInstanceOf[java.util.Map[String, Any]]
    val scalaPropertyMap = JavaConverters.mapAsScalaMap(javaPropertyMap)
    Configuration(scalaPropertyMap)

  }
}

class IllegalConfigurationException(message: String, cause: Throwable) extends RuntimeException(message,cause) {
  private val serialVersionUID = 695506964810499989L

}

object IllegalConfigurationException {
  def apply(message: String): IllegalConfigurationException = {
    new IllegalConfigurationException(message, null)
  }
}
