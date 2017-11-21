package com.xten.tide.runtime.runtime.jar

import java.net.ServerSocket

import org.slf4j.LoggerFactory

/**
  * Description: 
  * User: kongqingyu
  * Date: 2017/10/19 
  */
object FileServer {

  val LOG = LoggerFactory.getLogger(FileServer.getClass)

  private var serverSocket : ServerSocket = null

  def startServer(port : Int) = {

    new Thread(new Runnable {
      override def run(): Unit = {

        LOG.info(s"start FileServer,port is ${port}")

        if (serverSocket != null ){
          return
        }

        serverSocket = new ServerSocket(port);

        while (true){

          val socket = serverSocket.accept()


        }




      }
    })

  }


}
