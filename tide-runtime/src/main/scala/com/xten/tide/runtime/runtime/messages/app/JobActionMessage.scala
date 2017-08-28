package com.xten.tide.runtime.runtime.messages.app

import com.xten.tide.runtime.api.graph.{StreamGraph}
import com.xten.tide.runtime.runtime.messages.ActionMessage

import scala.collection.mutable

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/24 
  */


case class AppUpAction(streamGraph: StreamGraph) extends ActionMessage

case class AppReadyAction() extends ActionMessage

case class AppRemoveAction()