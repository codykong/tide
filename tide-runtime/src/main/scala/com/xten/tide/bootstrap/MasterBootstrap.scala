package com.xten.tide.bootstrap

import com.typesafe.config.Config
import com.xten.tide.configuration.{ClusterOptions, ConfigConstants, Configuration, GlobalConfiguration}
import com.xten.tide.runtime.runtime.appmanager.{AppManager, AppManagerCliOptions}
import com.xten.tide.runtime.runtime.appmanager.AppManagerMode.AppManagerMode
import com.xten.tide.runtime.runtime.resourcemanager.{ResourceContext, ResourceManager}
import com.xten.tide.runtime.util.EnvironmentInformation
import com.xten.tide.web.WebMonitor
import org.slf4j.LoggerFactory

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/16 
  */
object MasterBootstrap {

  val LOG = LoggerFactory.getLogger(MasterBootstrap.getClass)

  val STARTUP_FAILURE_RETURN_CODE = 1
  val RUNTIME_FAILURE_RETURN_CODE = 2

  def main(args: Array[String]): Unit = {

    LOG.info("MasterBootstrap start")

    EnvironmentInformation.logEnvironmentInfo(LOG,"Master",args)

    val (configuration : Configuration,
        executionMode : AppManagerMode,
        hostName : String,
        portRange : Int,
      webUIPort : Int) =
      try {
        parseArgs(args)
      }catch {
        case e: Exception =>
          LOG.error(e.getMessage,e)
          System.exit(STARTUP_FAILURE_RETURN_CODE)
          null
      }

    val (appManagerSystem,appManager,resourceManager) = AppManager.startActorSystemAndAppMangerActors(hostName,portRange,configuration)

    WebMonitor.start(appManagerSystem,configuration,hostName,webUIPort)

  }


  /**
    * Loads the configuration, execution mode and the listening address from the provided command
    * line arguments.
    *
    * @param args command line arguments
    * @return Quadruple of configuration, execution mode and an optional listening address
    */
  def parseArgs(args : Array[String]) : (Configuration , AppManagerMode ,String ,Int,Int) = {

    val parser = new scopt.OptionParser[AppManagerCliOptions]("AppManager") {
      head("Tide AppManager")

      opt[String]("configDir") action {(arg, conf) =>
        conf.setConfigDir(arg)
        conf
      }

      opt[String]("executionMode") action {(arg ,conf) =>
        conf.setAppManagerMode(arg)
        conf
      }

      opt[String]("host") action {(arg ,conf) =>
        conf.setHost(arg)
        conf
      }

      opt[Int]("webui-port") action {(arg ,conf) =>
        conf.setWebUIPort(arg)
        conf
      }
    }

    val cliOptions = parser.parse(args, new AppManagerCliOptions()).getOrElse {
      throw new Exception(
        s"Invalid command line arguments: ${args.mkString(" ")}. Usage: ${parser.usage}")
    }

    val configDir = cliOptions.configDir

    if (configDir == null) {
      throw new Exception("Missing parameter '--configDir'")
    }
    if (cliOptions.getAppManagerMode() == null) {
      throw new Exception("Missing parameter '--executionMode'")
    }

    LOG.info("Loading configuration from " + configDir)
    val configuration = GlobalConfiguration.loadConfiguration(configDir)

    val port = configuration.getInt( ClusterOptions.RPC_MASTER_PORT_OPTIONS)

    (configuration,cliOptions.getAppManagerMode(),cliOptions.host,port,cliOptions.webUIPort)




  }


}
