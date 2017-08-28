package com.xten.tide.runtime.runtime.taskGraph

import java.util.UUID

import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.runtime.jobgraph.Task

import scala.collection.immutable.List
import scala.collection.mutable

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/4 
  */
class TaskGraph() {

  var taskId :String =UUID.randomUUID().toString

  var componentNodes= new mutable.HashSet[Task]

  /** Set of JAR files required to run this job. */
  private val userJars : List[String]= List.empty

  /** List of classpaths required to run this job. */
  private var classpaths : List[String] = List.empty

  private var configuration:Configuration = _

}
