package com.xten.tide.bootstrap

import java.io.IOException

import com.xten.tide.configuration.{ConfigConstants, Configuration, GlobalConfiguration, IllegalConfigurationException}
import com.xten.tide.runtime.runtime.nodemanager.{NodeContext, NodeManager, NodeManagerCliOptions}
import com.xten.tide.runtime.runtime.resourcemanager.{ResourceContext, ResourceManager}
import com.xten.tide.runtime.util.{AddressUtil, EnvironmentInformation, NetUtils}
import org.slf4j.LoggerFactory

import scala.util.control.Breaks

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/16 
  */
object NodeBootstrap {

  private val LOG = LoggerFactory.getLogger(NodeBootstrap.getClass)

  /** Return code for unsuccessful NodeManager startup */
  val STARTUP_FAILURE_RETURN_CODE = 1

  def main(args: Array[String]): Unit = {

    EnvironmentInformation.logEnvironmentInfo(LOG,"NodeManager",args)

    // try to parse the command line arguments
    val configuration: Configuration = try {
      parseArgsAndLoadConfig(args)
    }
    catch {
      case t: Throwable =>
        LOG.error(t.getMessage(), t)
        System.exit(STARTUP_FAILURE_RETURN_CODE)
        null
    }

    val (nodeManagerHostname,port) = selectHostAndPort(configuration)

    val nodeManagerActorSystem = NodeManager.startNodeManagerActorSystem(configuration,nodeManagerHostname,port)
    NodeManager.startNodeManager(nodeManagerActorSystem,configuration)

  }

  /**
    * 获取启动所需的host 和 port
    * @param configuration
    * @throws IllegalConfigurationException
    * @return
    */
  @throws(classOf[IllegalConfigurationException])
  def selectHostAndPort(configuration: Configuration) : (String,Int) = {
    var nodeManagerHostname = configuration.getOption[String](ConfigConstants.NODE_MANAGER_IPC_ADDRESS_KEY,None)

    if (nodeManagerHostname.isDefined) {
      LOG.info("Using configured hostname/address for NodeManager: " + nodeManagerHostname)
    }else {
      nodeManagerHostname = Some(AddressUtil.getHostIP)
      LOG.info("Using InetAddress hostname/address for NodeManager: " + nodeManagerHostname)
    }

    // if no task manager port has been configured, use 0 (system will pick any free port)
    var actorSystemPort = configuration.getInt(ConfigConstants.NODE_MANAGER_IPC_PORT_KEY, Some(0))
    if (actorSystemPort < 0 || actorSystemPort > ConfigConstants.DEFAULT_DYNAMIC_RPC_MAX_PORT) {
      throw IllegalConfigurationException(s"Invalid port for ${nodeManagerHostname.get},port is ${actorSystemPort}")
    }else if (actorSystemPort == 0) {
      Breaks.breakable{
        for (i <- ConfigConstants.DEFAULT_DYNAMIC_RPC_MIN_PORT to ConfigConstants.DEFAULT_DYNAMIC_RPC_MAX_PORT) {
          if(NetUtils.isIdlePort(nodeManagerHostname.get,i)){
            actorSystemPort = i
            Breaks.break()
          }
        }
      }
    }

    (nodeManagerHostname.get, actorSystemPort)

  }


  def parseArgsAndLoadConfig(args: Array[String]): Configuration = {

    // set up the command line parser
    val parser = new scopt.OptionParser[NodeManagerCliOptions]("NodeManager") {
      head("Tide NodeManager")

      opt[String]("configDir") action { (param, conf) =>
        conf.setConfigDir(param)
        conf
      } text {
        "Specify configuration directory."
      }
    }



    // parse the CLI arguments
    val cliConfig = parser.parse(args, new NodeManagerCliOptions()).getOrElse {
      throw new Exception(
        s"Invalid command line arguments: ${args.mkString(" ")}. Usage: ${parser.usage}")
    }

    LogConfigLoader.loadLogFile(ConfigConstants.TIDE_LOG_CONF_FILENAME,cliConfig.getConfigDir);

    // load the configuration
    val conf: Configuration = try {
      LOG.info("Loading configuration from " + cliConfig.getConfigDir())
      GlobalConfiguration.loadConfiguration(cliConfig.getConfigDir())
    }
    catch {
      case e: Exception => throw new Exception("Could not load configuration", e)
    }

    conf
  }

}
