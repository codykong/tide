package com.xten.tide.client.program

import java.io.{File, IOException}
import java.net.{URISyntaxException, URL}


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/24 
  */
object JobWithJars {

  @throws[IOException]
  def checkJarFile(jar: URL) {
    var jarFile : File = null
    try{
      jarFile = new File(jar.toURI)

    }catch {
      case e: URISyntaxException => {
        throw new IOException("JAR file path is invalid '" + jar + "'")
      }
    }
    if (!jarFile.exists) throw new IOException("JAR file does not exist '" + jarFile.getAbsolutePath + "'")
    if (!jarFile.canRead) throw new IOException("JAR file can't be read '" + jarFile.getAbsolutePath + "'")
  }

  def buildUserCodeClassLoader(jars: List[URL], classpaths: List[URL], parent: ClassLoader): TideUserClassLoader = {
    val urls = new Array[URL](jars.size + classpaths.size)
    for (i <- 0 until jars.size) {
      urls.update(i,jars(i))
    }
    for (i <- 0 until classpaths.size) {
      urls.update(i+jars.size,classpaths(i))

    }
    new TideUserClassLoader(urls, parent)
  }

}
