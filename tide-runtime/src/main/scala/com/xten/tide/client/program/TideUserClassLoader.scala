package com.xten.tide.client.program

import java.net.URL

import scala.reflect.internal.util.ScalaClassLoader.URLClassLoader

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/24 
  */
class TideUserClassLoader(urls: Array[URL], parent: ClassLoader) extends URLClassLoader(urls,parent){

}

object TideUserClassLoader {
  def apply(urls: Array[URL]): TideUserClassLoader = {
    new TideUserClassLoader(urls, TideUserClassLoader.getClass.getClassLoader)
  }
}
