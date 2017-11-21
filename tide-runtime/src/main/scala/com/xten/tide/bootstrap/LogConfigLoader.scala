package com.xten.tide.bootstrap

import java.io.{File, FileInputStream}

import org.apache.logging.log4j.core.config.{ConfigurationSource, Configurator}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Description: 
  * User: kongqingyu
  * Date: 2017/9/27 
  */
object LogConfigLoader {

  /**
    * 加载外部路径下的log 配置文件
    * @param logFile
    * @param configDir
    */
  def loadLogFile(logFile : String, configDir : String) = {

    if (configDir == null) throw new IllegalArgumentException("Given configuration directory is null, cannot load configuration")

    val confDirFile = new File(configDir)
    if (!confDirFile.exists) throw new IllegalArgumentException("The given configuration directory name '" + configDir + "' (" + confDirFile.getAbsolutePath + ") does not describe an existing directory.")

    val logConfigFile = new File(confDirFile, logFile)

    val source = new ConfigurationSource(new FileInputStream(logConfigFile), logConfigFile)
    Configurator.initialize(null, source)

    val LOG = LoggerFactory.getLogger(LogConfigLoader.getClass)
    LOG.info("log start")
  }


}
