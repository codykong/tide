package com.xten.tide.runtime.any


import scala.collection.mutable

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/24 
  */
object ScalaStream extends App{

  val map = new mutable.HashMap[Int,Int]()

  for(i <- 1 to 10){
    map.put(i,i*2)
  }

  val newMap = map.filter(p => p._1%2==0).toMap

  println(newMap)
  println(map)

}
