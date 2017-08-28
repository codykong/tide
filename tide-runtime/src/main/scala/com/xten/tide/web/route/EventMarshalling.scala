package com.xten.tide.web.route

import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, FromRequestUnmarshaller, PredefinedFromEntityUnmarshallers, Unmarshaller}
import com.google.gson.Gson
import spray.json.DefaultJsonProtocol

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/25 
  */
trait EventMarshalling  extends DefaultJsonProtocol{

  implicit val ipPairSummaryRequestFormat = jsonFormat4(DeployTide)


}


case class DeployTide(name : String,parallelism: Int ,entryClass : String,jarName : String)
