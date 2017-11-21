package com.xten.tide.web.route

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server._
import akka.util.{ByteString, Timeout}
import com.xten.tide.runtime.runtime.akka.TimeoutConstant
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MediaTypes}
import akka.stream.scaladsl.Framing
import com.xten.tide.configuration.Configuration
import com.xten.tide.runtime.runtime.messages.SuccessActionRes
import com.xten.tide.web.handler.{Handlers, JarRunHandler}
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/25 
  */
class RestApi(actorSystem : ActorSystem,timeout : Timeout) extends EventMarshalling{


  implicit val requestTimeout = timeout

  implicit def executionContext = actorSystem.dispatcher

  def routes : Route = route

  def route = {
    pathPrefix("deploy") {
      pathEndOrSingleSlash {
        post {
          entity(as[DeployTide]) { ed =>
            onSuccess(DeployRoute.run(ed)) {
              case res: SuccessActionRes => complete {
                res.toString
              }
            }

          }
        }
      }
    }~
    pathPrefix("index") {
      pathEndOrSingleSlash {
        val entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, DeployRoute.uploadPage())
        complete(entity)
      }
    }~
      path("upload") {
        extractRequestContext { ctx =>
          implicit val materializer = ctx.materializer
          implicit val ec = ctx.executionContext

          fileUpload("csv") {
            case (metadata, byteSource) =>

              val sumF: Future[Int] =
              // sum the numbers as they arrive so that we can
              // accept any size of file
                byteSource.via(Framing.delimiter(ByteString("\n"), 1024))
                  .mapConcat(_.utf8String.split(",").toVector)
                  .map(_.toInt)
                  .runFold(0) { (acc, n) => acc + n }

              onSuccess(sumF) { sum => complete(s"Sum: $sum") }
          }
        }
      }
  }

}



object RestApi {

  private val LOG = LoggerFactory.getLogger(RestApi.getClass)

  def main(args: Array[String]): Unit = {
    val system = ActorSystem()

    bind(system,"127.0.0.1",8080,Configuration.apply())
  }

  var bindingFuture : Option[Future[ServerBinding]]= None



  def bind(actorSystem : ActorSystem,host : String ,port : Int,configuation: Configuration) = {

    implicit val system = actorSystem
    implicit val ec = system.dispatcher


    val api = new RestApi(actorSystem,TimeoutConstant.SYSTEM_MSG_TIMEOUT).routes

    implicit val materializer = ActorMaterializer()

    bindingFuture = try {
       Some(Http().bindAndHandle(api, host, port))
    }catch {
      case e: Exception => {
        LOG.error(s"start webUi error, ${e.getMessage}",e)
        throw e
      }
    }

    Handlers.initHandlers(configuation,actorSystem)

  }

  def unbind() = {

    if (bindingFuture.isDefined) {
          bindingFuture.get
            .flatMap(_.unbind()) // trigger unbinding from the port
    }
  }

}
