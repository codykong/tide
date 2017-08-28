package com.xten.tide.runtime.runtime.jobgraph

import java.nio.file.Path
import java.util.UUID

import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.api.graph.{StreamGraph, StreamNode}
import com.xten.tide.runtime.runtime.jobgraph

import scala.collection.immutable.List
import scala.collection.mutable


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/5/16 
  */
class JobGraphGenerator(streamGraph : StreamGraph){

  var nodeId :Int = 0



  def createJobGraph(): JobGraph ={
    val jobGraph = new JobGraph(streamGraph.getJobName)

    var tasks = new mutable.HashSet[Task]

    for (node <- streamGraph.streamNodes){
      tasks ++= Task.transformTask(node._2)
    }

    jobGraph.tasks = tasks

    jobGraph
  }




}

object JobGraphGenerator{
  def createJobGraph(streamGraph : StreamGraph):JobGraph = {
    val jobGraphGenerator = new JobGraphGenerator(streamGraph)
    val jobGraph = jobGraphGenerator.createJobGraph()

    jobGraph
  }
}

class JobGraph(jobName :String) {

  var jobId :String =UUID.randomUUID().toString

  var tasks= new mutable.HashSet[Task]

  /** Set of JAR files required to run this job. */
  private val userJars : List[String]= List.empty

  /** List of classpaths required to run this job. */
  private var classpaths : List[String] = List.empty

  private var configuration:Configuration = _

  def addTask(task: Task) = {
    tasks.add(task)
  }

}


