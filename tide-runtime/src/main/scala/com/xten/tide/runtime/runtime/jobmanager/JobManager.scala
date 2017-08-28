package com.xten.tide.runtime.runtime.jobmanager

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import com.typesafe.config.Config
import com.xten.tide.api.functions.BaseFunction
import com.xten.tide.runtime.api.functions.MapFunction
import com.xten.tide.runtime.api.functions.source.SourceFunction
import com.xten.tide.runtime.runtime.akka.AkkaUtils
import com.xten.tide.runtime.runtime.component.{ComponentContext, MapComponent, SourceComponent}
import com.xten.tide.runtime.runtime.jobgraph.Task
import com.xten.tide.runtime.runtime.messages.cluster._
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/23 
  */
class JobManager(jobManagerContext: JobManagerContext) extends Actor() {

  val Logger = LoggerFactory.getLogger(this.getClass)

  var sons :mutable.Map[String,ActorRef] = new mutable.HashMap[String,ActorRef]()

  override def receive: Receive = {
    case action: MemberUpAction => {
      for(task <- action.tasks){
        startComponent(task)
      }
    }
    case action : MemberRemoveAction => {
      if (sons.get(action.task.taskId).isDefined){
        sons.get(action.task.taskId).get ! PoisonPill
      }
    }
    case member : MemberUpped => {
      Logger.info(s"MemberUpped is ${member.member}")
      val appMasterPathSelection = context.system.actorSelection(jobManagerContext.appMasterPath)

      appMasterPathSelection ! member
    }
    case member : MemberRemoved => {
      Logger.info(s"MemberRemoved is ${member.member}")
    }

  }

  /**
    * 启动各个组件
    * @param task
    */
  private def startComponent(task:Task): Unit ={
    val function = task.jobVertexClass.newInstance()


    function match {
      case mapFunction: MapFunction => {
        val componentContext = ComponentContext[BaseFunction](mapFunction, task)
        val actorRef = AkkaUtils.createComponent(context,classOf[MapComponent],componentContext)
        sons.put(task.taskId,actorRef)
      }
      case function : SourceFunction => {

        val componentContext = ComponentContext[BaseFunction](function, task)
        val actorRef = AkkaUtils.createComponent(context,classOf[SourceComponent],componentContext)
        sons.put(task.taskId,actorRef)

      }
      case any :Any => {
        println(any)
    }
//      case sourceFunction: SourceFunction => {
//        val context = TaskContext[BaseFunction](sourceFunction, componentNode.executionConfig,componentNode.receivers)
//        val future = ask(taskManagerActor, new MemberUpAction(context)).mapTo[MemberUpped]
//        future.onComplete {
//          case Success(result) => println(s"context is ${context},result is ${result}")
//        }
//      }
//      case sinkFunction: SinkFunction => {
//        val componentContext = ComponentContext[SinkFunction](sinkFunction, componentNode)
//        AkkaUtils.createComponent(context,classOf[MapComponent],componentContext)
//      }
    }
  }
}

object JobManager{
  val DEFAULT_JOB_MANAGER_NAME = "JobManager"


  def startJobManager(system: ActorSystem,jobManagerContext: JobManagerContext): ActorRef = {
    system.actorOf(Props.create(classOf[JobManager],jobManagerContext),name=DEFAULT_JOB_MANAGER_NAME)
  }


  def startJobManagerActorSystem(jobId :String, config:Config):ActorSystem = {
    AkkaUtils.createActorSystem(jobId,config)
  }

}
