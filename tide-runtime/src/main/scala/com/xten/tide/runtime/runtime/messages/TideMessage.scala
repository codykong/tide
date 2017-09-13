package com.xten.tide.runtime.runtime.messages

import akka.dispatch.ControlMessage
import com.xten.tide.runtime.api.operators.OperatorTypeEnum.Value
import com.xten.tide.runtime.runtime.messages.ResponseMessageCodeEnum.ResponseMessageCodeEnum

import scala.util.Failure

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/8 
  */
trait TideMessage extends Serializable with ControlMessage{

}

/**
  * 请求类消息，需要使用ask模式发送，并需要返回ResponseMessage
  */
trait RequestMessage extends TideMessage

/**
  * 应答类消息，使用send模式发送，对RequestMessage的返回结果
  */
trait ResponseMessage extends TideMessage {
  var code : ResponseMessageCodeEnum = ResponseMessageCodeEnum.Success
  var msg : String = ""


}

/**
  * 操作类型消息，执行一定的操作
  */
trait ActionMessage extends TideMessage

/**
  * 通知类消息，进行通知
  */
trait NoticeMessage extends TideMessage




class ActionRes(code : Int,msg :String) extends NoticeMessage{
  def isSuccess():Boolean ={
    code== ActionRes.ACTION_SUCCESS
  }

  Failure
}

case class SuccessActionRes(val msg : String = "成功") extends ActionRes(ActionRes.ACTION_SUCCESS,msg)
case class FailureActionRes(val msg : String = "失败") extends ActionRes(ActionRes.ACTION_FAIL,msg)

object ResponseMessageCodeEnum extends Enumeration{

  type ResponseMessageCodeEnum = Value
  val Success = Value(1, "Success")
  val Fail = Value(0, "Fail")
}

object ActionRes{
  val ACTION_SUCCESS_MESSAGE = "success"
  val ACTION_SUCCESS = 1
  val ACTION_FAIL = 0

  def defaultSuccess(): ActionRes ={
    new SuccessActionRes()
  }

  def success(msg:String): ActionRes ={
    new SuccessActionRes(msg)
  }

  def fail(msg:String): ActionRes ={
    new FailureActionRes(msg)
  }
}

object SuccessActionResTest extends App {
  val res =new SuccessActionRes()
  println(res.msg)
}


