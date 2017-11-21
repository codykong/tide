package com.xten.tide.configuration

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/5 
  */
object ConfigConstants {

  val TIDE_LOG_CONF_FILENAME = "log4j2.xml"

  // ------------------------------------------------------------------------
  //  流控
  // ------------------------------------------------------------------------

  /**
    * 初始化最大发送速率
    */
  val FLOW_CONTROL_MAX_SEND_RATE = "flowcontrol.initial-max-send-rate"
  /**
    * 发送统计时间间隔
    */
  val FLOW_CONTROL_SEND_COLLECT_INTERVAL = "flowcontrol.send-collect-interval"
  /**
    * 初始化流控最大处理速率
    */
  val FLOW_CONTROL_MAX_DEAL_RATE = "flowcontrol.initial-max-deal-rate"
  /**
    * 处理统计时间间隔
    */
  val FLOW_CONTROL_DEAL_COLLECT_INTERVAL  = "flowcontrol.deal-collect-interval"

  // ------------------------------ AKKA ------------------------------------
  /**
    * akka 远程netty的TCP端口
    */
  val AKKA_REMOTE_NETTY_TCP_PORT ="akka.remote.netty.tcp.port"
  /**
    * AKKA 远程netty的host
    */
  val AKKA_REMOTE_NETTY_TCP_HOSTNAME ="akka.remote.netty.tcp.hostname"
  /**
    * akka 基本配置文件的路径
    */
//  val AKKA_BASIC_CONFIG_PATH = "akka_basic.conf"
  /**
    * akka remote模式基本配置文件的路径
    */
  val AKKA_REMOTE_BASIC_CONFIG_PATH = "akka_remote.conf"
  /**
    * Timeout for the startup of the actor system
    */
  val AKKA_STARTUP_TIMEOUT = "akka.startup-timeout"
  /**
    * Heartbeat interval of the transport failure detector
    */
  val AKKA_TRANSPORT_HEARTBEAT_INTERVAL = "akka.transport.heartbeat.interval"
  /**
    * Allowed heartbeat pause for the transport failure detector
    */
  val AKKA_TRANSPORT_HEARTBEAT_PAUSE = "akka.transport.heartbeat.pause"
  /**
    * Detection threshold of transport failure detector
    */
  val AKKA_TRANSPORT_THRESHOLD = "akka.transport.threshold"
  /**
    * Heartbeat interval of watch failure detector
    */
  val AKKA_WATCH_HEARTBEAT_INTERVAL = "akka.watch.heartbeat.interval"
  /**
    * Allowed heartbeat pause for the watch failure detector
    */
  val AKKA_WATCH_HEARTBEAT_PAUSE = "akka.watch.heartbeat.pause"
  /**
    * Detection threshold for the phi accrual watch failure detector
    */
  val AKKA_WATCH_THRESHOLD = "akka.watch.threshold"
  /**
    * Akka TCP timeout
    */
  val AKKA_TCP_TIMEOUT = "akka.tcp.timeout"
  /**
    * Override SSL support for the Akka transport
    */
  val AKKA_SSL_ENABLED = "akka.ssl.enabled"
  /**
    * Maximum framesize of akka messages
    */
  val AKKA_FRAMESIZE = "akka.framesize"
  /**
    * Maximum number of messages until another actor is executed by the same thread
    */
  val AKKA_DISPATCHER_THROUGHPUT = "akka.throughput"
  /**
    * Log lifecycle events
    */
  val AKKA_LOG_LIFECYCLE_EVENTS = "akka.log.lifecycle.events"

  /**
    * log级别
    */
  val AKKA_LOG_LEVEL ="akka.loglevel"
  /**
    * Timeout for all blocking calls on the cluster side
    */
  val AKKA_ASK_TIMEOUT = "akka.ask.timeout"
  /**
    * Timeout for all blocking calls that look up remote actors
    */
  val AKKA_LOOKUP_TIMEOUT = "akka.lookup.timeout"
  /**
    * Timeout for all blocking calls on the client side
    */
  val AKKA_CLIENT_TIMEOUT = "akka.client.timeout"
  /**
    * Exit JVM on fatal Akka errors
    */
  val AKKA_JVM_EXIT_ON_FATAL_ERROR = "akka.jvm-exit-on-fatal-error"
  // ----------------------------- LocalExecution ----------------------------
  /**
    * Sets the number of local task managers
    */
  val LOCAL_NUMBER_TASK_MANAGER = "local.number-taskmanager"
  val DEFAULT_LOCAL_NUMBER_TASK_MANAGER = 1
  val LOCAL_NUMBER_JOB_MANAGER = "local.number-jobmanager"
  val DEFAULT_LOCAL_NUMBER_JOB_MANAGER = 1
  val LOCAL_NUMBER_RESOURCE_MANAGER = "local.number-resourcemanager"
  val DEFAULT_LOCAL_NUMBER_RESOURCE_MANAGER = 1
  val LOCAL_START_WEBSERVER = "local.start-webserver"

  /**
    * 配置的集群名称
    */
  val CLUSTER_NAME_KEY = "cluster.name"

  /**
    * 默认集群名称
    */
  val DEFAULT_CLUSTER_NAME = "MyTide"



  /**
    * The default network port to connect to for communication with the job manager.
    */
  val DEFAULT_JOB_MANAGER_IPC_PORT = 10241

  /**
    * 可是使用的最大的jobmanager端口号
    */
  val DEFAULT_JOB_MANAGER_IPC_PORT_MAX = 65000

  /**
    * appMaster 通信所需要的端口
    */
  val APP_MASTER_IPC_PORT_KEY = "appmaster.rpc.port"


  /**
    * appMaster 通信所需要的地址
    */
  val APP_MASTER_IPC_ADDRESS_KEY = "appmaster.rpc.address"

  /**
    * The default network port to connect to for communication with the job manager.
    */
  val DEFAULT_RESOURCE_MANAGER_IPC_PORT = 2553

  /**
    * resourcemanager 通信所需要的端口
    */
  val RESOURCE_MANAGER_IPC_PORT_KEY = "resourcemanager.rpc.port"


  /**
    * resourcemanager 通信所需要的地址
    */
  val RESOURCE_MANAGER_IPC_ADDRESS_KEY = "resourcemanager.rpc.address"

  /**
    * nodemanager 通信所需要的端口
    */
  val NODE_MANAGER_IPC_PORT_KEY = "nodemanager.rpc.port"


  /**
    * nodemanager 通信所需要的地址
    */
  val NODE_MANAGER_IPC_ADDRESS_KEY = "nodemanager.rpc.address"

  val JOB_MANAGER_ACTOR_SYSTEM_NAME = "jobmanager.actorsystem.name"

  /**
    * task 通信所需要的端口
    */
  val TASK_IPC_PORT_KEY = "task.rpc.port"


  /**
    * task 通信所需要的地址
    */
  val TASK_IPC_ADDRESS_KEY = "task.rpc.address"

  /**
    * Cluster 注册的种子节点
    */
  val MASTER_SEED_NODES_RPC_KEY = "master.seed-nodes.rpc"

  /**
    * Master节点的通信port
    */
  val MASTER_RPC_PORT_KEY = "master.rpc.port"

  /**
    * Master节点的通信地址
    */
  val MASTER_RPC_ADDRESS_KEY = "master.rpc.address"

  /**
    * Master WebUI port
    */
  val MASTER_WEB_PORT_KEY = "master.web.port"
  /**
    * The default network port to connect to for communication with the job manager.
    */
  val DEFAULT_MASTER_RPC_PORT = 6123

  /**
    * 动态生成端口时，判断的起始端口，此端口段空闲较多
    */
  val DEFAULT_DYNAMIC_RPC_MIN_PORT = 10241

  /**
    * 动态生成端口时，判断的最大端口，此端口段空闲较多
    */
  val DEFAULT_DYNAMIC_RPC_MAX_PORT = 65535

  /**
    * 本地资源路径
    */
  val LOCAL_RESOURCE_PATH_KEY = "local.resource.path"

  /**
    * Master节点的通信地址
    */
  val DEFAULT_LOCAL_RESOURCE_PATH = "/Users/xten/MySpace/github/xten.tide/tide-examples/tide-examples-function/target"

  /**
    * 执行并行度
    */
  val EXECUTION_PARALLELISM_KEY = "execution_parallelism"


}
