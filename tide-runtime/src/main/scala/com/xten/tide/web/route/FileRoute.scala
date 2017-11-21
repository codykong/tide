package com.xten.tide.web.route

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.{Marshal, ToResponseMarshallable}
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, MediaTypes, Multipart, _}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import spray.json.{JsonFormat, RootJsonFormat}

import scala.concurrent.{ExecutionContext, Future}
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.server.directives.FileInfo
import akka.stream.Attributes.Name
/**
  * Description: 
  * User: kongqingyu
  * Date: 2017/10/18 
  */
object FileRoute {

//  implicit def fileInfoFormat: JsonFormat[FileInfo] = jsonFormat3(FileInfo.apply)

//  class Client(system: ActorSystem, host: String, port: Int) {
//    private implicit val actorSystem = system
//    private implicit val materializer = ActorMaterializer()
//    private implicit val ec = system.dispatcher
//
//    val server = Uri(s"http://$host:$port")
//    val httpClient = Http(system).outgoingConnection(server.authority.host.address(), server.authority.port)
//
//    case class FileHandle(private[Client] val info: FileInfo)
//
//    def upload(file: File): Future[FileHandle] = {
//      val target = server.withPath(Path("/upload"))
//
//      val request = entity(file).map{entity =>
//        HttpRequest(HttpMethods.POST, uri = target, entity = entity)
//      }
//
//      val response = Source(request).via(httpClient).runWith(Sink.head)
//      response.flatMap(some => Unmarshal(some).to[Map[Name, FileInfo]]).map(map => FileHandle(map.head._2))
//    }
//
//    def download(remoteFile: FileHandle, saveAs: File): Future[Unit] = {
//      val serverFile = remoteFile.info.targetFile
//      val downoad = server.withPath(Path("/download")).withQuery("file" -> serverFile)
//      //download file to local
//      val response = Source.single(HttpRequest(uri = downoad)).via(httpClient).runWith(Sink.head)
//      val downloaded = response.flatMap { response =>
//        response.entity.dataBytes.runWith(SynchronousFileSink(saveAs))
//      }
//      downloaded.map(written => Unit)
//    }
//
//    private def entity(file: File)(implicit ec: ExecutionContext): Future[RequestEntity] = {
//      val entity =  HttpEntity(MediaTypes.`application/octet-stream`, file.length(), SynchronousFileSource(file, chunkSize = 100000))
//      val body = Source.single(
//        Multipart.FormData.BodyPart(
//          "uploadfile",
//          entity,
//          Map("filename" -> file.getName)))
//      val form = Multipart.FormData(body)
//
//      Marshal(form).to[RequestEntity]
//    }
//  }
}
