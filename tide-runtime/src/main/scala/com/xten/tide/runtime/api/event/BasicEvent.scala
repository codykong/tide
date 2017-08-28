package com.xten.tide.runtime.api.event


case class IntEvent(val option : Option[Int]) extends IEvent{
  override def isEmpty(): Boolean = option.isEmpty

  override def isDefined(): Boolean = option.isDefined

  override def toString: String = {
    option.toString
  }
}

object IntEvent{
  def apply(value :Int): IntEvent = new IntEvent(Some(value))
  def apply(): IntEvent = new IntEvent(None)
}


case class EmptyEvent() extends IEvent{
  override def isEmpty(): Boolean = true

  override def isDefined(): Boolean = false
}


