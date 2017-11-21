package com.xten.tide.runtime.runtime.akka.mailbox

import akka.actor.{ActorRef, ActorSystem}
import com.xten.tide.runtime.runtime.akka.AkkaUtils

import scala.collection.mutable

/**
  * Description: 消息计数器
  * User: kongqingyu
  * Date: 2017/10/17
  */
object MessageCounter {

  /**
    * 邮箱消息计数器
    */
  private val mailboxMessageCounters = new mutable.HashMap[String,MessageCounter]()

  /**
    * 获取邮箱中的消息的数量
    * @param actorRef
    * @return
    */
  def getMailboxNumberOfMessages(actorRef: ActorRef,system: ActorSystem) : Option[Int] = {
    val key = AkkaUtils.remotePath(system,actorRef)

    val mailboxCounter = mailboxMessageCounters.get(key)

    if (mailboxCounter.isEmpty){
      None
    }else {
      Some(mailboxCounter.get.numberOfMessages())
    }


  }

  /**
    * 注册邮箱到相应的计数器中
    * @param owner
    * @param system
    * @return
    */
  def registerMailbox(owner: Option[ActorRef],system: Option[ActorSystem]) : Option[MessageCounter] = {
    if (owner.isEmpty || system.isEmpty){
      None
    }else {
      val key = AkkaUtils.remotePath(system.get,owner.get)

      if (mailboxMessageCounters.get(key).isEmpty){
        val messageCounter = new MessageCounter;
        mailboxMessageCounters.put(key,messageCounter)
        Some(messageCounter)
      }else {
        mailboxMessageCounters.get(key)
      }
    }
  }


}


class MessageCounter {
  /**
    * 入队邮箱计数器
    */
  private var enqueueMailboxCounter:Long = 0
  /**
    * 出队邮箱计数器
    */
  private var dequeueMailboxCounter:Long = 0

  def enqueue(): Unit = enqueueMailboxCounter += 1

  def dequeue(): Unit = dequeueMailboxCounter += 1


  def numberOfMessages(): Int= {
    (enqueueMailboxCounter - dequeueMailboxCounter).toInt
  }
}

