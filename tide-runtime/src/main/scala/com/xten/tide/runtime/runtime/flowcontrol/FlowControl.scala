package com.xten.tide.runtime.runtime.flowcontrol

import java.util.concurrent.atomic.LongAdder

import com.xten.tide.configuration.{Configuration, FlowControlOptions}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/9 
  */
trait FlowControl {

  protected var config : Configuration = Configuration.apply()

  /**
    * 加载配置文件
    */
  def loadConfig()

  /**
    * 默认发送统计时间间隔（毫秒）
    */
  val DEFAULT_SEND_COLLECT_INTERVAL_MILLIS = 1000

  /**
    * 默认处理统计时间间隔（毫秒）
    */
  val DEFAULT_DEAL_COLLECT_INTERVAL_MILLIS = 1000


  /**
    * 向下游的最大发送速率
    */
  var maxSendRate = config.getInt(FlowControlOptions.FLOW_CONTROL_MAX_SEND_RATE_OPTIONS)

  /**
    * 该actor的最大处理速率
    */
  var maxDealRate = config.getInt(FlowControlOptions.FLOW_CONTROL_MAX_DEAL_RATE_OPTIONS)

  /**
    * 已经处理的消息的数量
    */
  var dealMsgNum : LongAdder  = new LongAdder
  /**
    * 下次处理速率汇总时间
    */
  var nextDealCollectTime = System.currentTimeMillis()
  /**
    * 已经发送的消息的数量
    */
  var sendMsgNum : LongAdder  = new LongAdder
  /**
    * 下次发送速率汇总时间
    */
  var nextSendCollectTime = System.currentTimeMillis()



  /**
    * 分析流量，并进行相应的流量控制
    */
  def flowAnalyze(): Unit ={

    // 如果设定了最大发送速率，才会进行发送速率处理
    if (maxSendRate < Int.MaxValue){
      analyzeSendRate()
    }
  }

  private def analyzeSendRate()={

    val currentTimeMillis = System.currentTimeMillis()

    // 如果过了统计时间，则清零处理
    if (nextSendCollectTime < currentTimeMillis){
      // 下次统计时间
      nextSendCollectTime = currentTimeMillis + DEFAULT_SEND_COLLECT_INTERVAL_MILLIS
      sendMsgNum.reset()
    }else {
      sendMsgNum.increment()

      // 如果在统计时间内已达最大发送速率，则在剩余时间内休眠
      if (sendMsgNum.sum().compare(maxSendRate) > 0 ){
        Thread.sleep(nextSendCollectTime - currentTimeMillis)
      }
    }
  }

}
