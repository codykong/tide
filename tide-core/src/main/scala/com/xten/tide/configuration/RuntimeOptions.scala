package com.xten.tide.configuration

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/23 
  */



object FlowControlOptions {

  val FLOW_CONTROL_MAX_SEND_RATE_OPTIONS = ConfigOptions
    .key(ConfigConstants.FLOW_CONTROL_MAX_SEND_RATE)
    .defaultValue(Int.MaxValue)


  val FLOW_CONTROL_MAX_DEAL_RATE_OPTIONS = ConfigOptions
    .key(ConfigConstants.FLOW_CONTROL_MAX_DEAL_RATE)
    .defaultValue(Int.MaxValue)

}

object ClusterOptions {

  val CLUSTER_NAME_OPTIONS = ConfigOptions
    .key(ConfigConstants.CLUSTER_NAME_KEY)
      .defaultValue(ConfigConstants.DEFAULT_CLUSTER_NAME)

  val RPC_MASTER_PORT_OPTIONS = ConfigOptions
    .key(ConfigConstants.MASTER_RPC_ADDRESS_KEY)
    .defaultValue(ConfigConstants.DEFAULT_MASTER_RPC_PORT)

  val LOCAL_RESOURCE_PATH_OPTIONS = ConfigOptions.key(ConfigConstants.LOCAL_RESOURCE_PATH_KEY)
    .defaultValue(ConfigConstants.DEFAULT_LOCAL_RESOURCE_PATH)
}

