package com.xten.tide.runtime.runtime.akka.mailbox

import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

import akka.actor.{ActorRef, ActorSystem}
import akka.dispatch._
import com.typesafe.config.Config

import scala.collection.mutable

/**
  * Description: 
  * User: kongqingyu
  * Date: 2017/10/17 
  */
final case class FlowAwareMailbox() extends MailboxType with ProducesMessageQueue[FlowAwareMailbox.MessageQueue]{
  // this constructor will be called via reflection when this mailbox type
  // is used in the application config
  def this(settings: ActorSystem.Settings, config: Config) = this()

  def create(owner: Option[ActorRef], system: Option[ActorSystem]): MessageQueue = {

    val messageCounter = MessageCounter.registerMailbox(owner,system)
    new FlowAwareMailbox.MessageQueue(messageCounter)
  }
}


object FlowAwareMailbox {

  class MessageQueue(messageCounter : Option[MessageCounter]) extends QueueBasedMessageQueue with java.io.Serializable {
    val controlQueue: Queue[Envelope] = new ConcurrentLinkedQueue[Envelope]()
    val queue: Queue[Envelope] = new ConcurrentLinkedQueue[Envelope]()

    def enqueue(receiver: ActorRef, handle: Envelope): Unit = handle match {
      case envelope @ Envelope(_: ControlMessage, _) ⇒ controlQueue add envelope
      case envelope                                  ⇒ {
        queue add envelope
        if (messageCounter.isDefined) {
          messageCounter.get.enqueue()
        }
      }
    }

    def dequeue(): Envelope = {
      val controlMsg = controlQueue.poll()

      if (controlMsg ne null) controlMsg
      else {
        if (messageCounter.isDefined) {
          messageCounter.get.dequeue()
        }
        queue.poll()
      }
    }

    override def numberOfMessages: Int = {
      controlQueue.size() + queue.size()
    }

    override def hasMessages: Boolean = !(queue.isEmpty && controlQueue.isEmpty)
  }


}
