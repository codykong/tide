package com.xten.tide.runtime.runtime.jobmanager

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import com.typesafe.config.Config
import com.xten.tide.api.functions.BaseFunction
import com.xten.tide.runtime.api.functions.MapFunction
import com.xten.tide.runtime.api.functions.sink.SinkFunction
import com.xten.tide.runtime.api.functions.source.SourceFunction
import com.xten.tide.runtime.runtime.akka.AkkaUtils
import com.xten.tide.runtime.runtime.component.execute.MapComponent
import com.xten.tide.runtime.runtime.component.ComponentContext
import com.xten.tide.runtime.runtime.component.source.SourceComponent
import com.xten.tide.runtime.runtime.messages.cluster._
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/23 
  */
class JobManager(jobManagerContext: JobManagerContext) extends Actor() {

  val LOG = LoggerFactory.getLogger(this.getClass)
  var userClassLoader : ClassLoader = null
  var appMasterActorRef : ActorRef = null

  var sons :mutable.Map[String,ActorRef] = new mutable.HashMap[String,ActorRef]()

  override def preStart(): Unit = {
    super.preStart()
    userClassLoader = Thread.currentThread().getContextClassLoader
    appMasterActorRef = AkkaUtils.pathToActorRef(jobManagerContext.appMasterPath)
    appMasterActorRef ! NodeMemberUpAction
    jobManagerContext.tasks.map(p => startComponent(p))

  }

  override def receive: Receive = {
    case action: TaskMemberUpAction => {
      for(task <- action.tasks){
        startComponent(task)
      }
    }
    case action : TaskMemberRemoveAction => {
      if (sons.get(action.task.taskId).isDefined){
        sons.get(action.task.taskId).get ! PoisonPill
      }
    }
    case member : TaskMemberUpped => {
      sons.put(member.member.taskId,context.sender())

      appMasterActorRef ! member
    }
    case member : TaskMemberRemoved => {
    }

  }

  /**
    * 启动各个组件
    * @param task
    */
  private def startComponent(task:Task): Unit ={

    Class.forName(task.className, true,userClassLoader)
    val function = Class.forName(task.className,true,Thread.currentThread().getContextClassLoader).newInstance()


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
      case function: SinkFunction => {
        val componentContext = ComponentContext[BaseFunction](function, task)
        val actorRef = AkkaUtils.createComponent(context,classOf[SourceComponent],componentContext)
        sons.put(task.taskId,actorRef)
      }
      case any :Any => {
        LOG.info(s"Function is ${any}")
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
