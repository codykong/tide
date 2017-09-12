package com.xten.tide.web.handler

import java.io.File
import java.net.URL

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.xten.tide.client.program.{ClusterClient, PackagedProgram}
import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.api.environment.ExecutionEnvironment
import com.xten.tide.runtime.runtime.appmanager.AppManager
import com.xten.tide.runtime.runtime.messages.{ActionMessage, ActionRes, SuccessActionRes, TideMessage}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/24 
  */
class JarRunHandler(configuration: Configuration) extends Actor{

  override def receive: Receive = {
    case action: JarStartAction => {
      val sender = context.sender()
      startJar(action)
      println(s"receive a jar start action , name is ${action.name}")
      sender ! SuccessActionRes()
    }
    case _ => {

    }
  }

  /**
    * 启动jar
    * @param action
    * @return
    */
  def startJar(action:JarStartAction) : ActionRes = {

    getGraphAndClassLoader(action)

    SuccessActionRes()
  }


  private def getGraphAndClassLoader(action:JarStartAction) = {

    val jarFile = new File(action.jarPath)

    val program = new PackagedProgram(jarFile,List.empty[URL],action.entryClass)

    val parentClassLoader = this.getClass.getClassLoader

    val userClassLoader = program.getUserCodeClassLoader()

    val plan = ClusterClient.getPlan(program,1)

    AppManager.submitPlan(plan)










  }



}

object JarRunHandler {
  val JAR_RUN_HANDLER_NAME = "jarRunHandler"


  /**
    * 启动JarRunHandler
    * @param configuration
    * @param appManagerSystem
    * @return
    */
  def startJarRunHandlerActor(configuration: Configuration ,
                            appManagerSystem : ActorSystem): ActorRef = {

    val webRouteClass = classOf[JarRunHandler]

    val webRouteProps = getJarRunHandlerProps(webRouteClass,configuration)

    appManagerSystem.actorOf(webRouteProps,JAR_RUN_HANDLER_NAME)

  }


  /**
    * 构造JarRunHandler的Props
    * @param webRouteClass
    * @return
    */
  def getJarRunHandlerProps(
                          webRouteClass: Class[_ <: JarRunHandler],
                          configuration: Configuration):Props = {

    Props(webRouteClass,
      configuration)

  }
}

case class JarStartAction(name : String,parallelism: Int ,entryClass : String,jarPath : String) extends ActionMessage