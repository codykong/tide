package com.xten.tide.runtime.runtime.akka

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/28 
  */
object FutureTest extends App{

  var futures = for (i <- 1 to 12) yield Future {
    println("Executing task " + i)
    Thread.sleep(i * 100L)
    throw new IllegalArgumentException()
    i * i
  }

  val aggregate = Future.sequence(futures)

  val squares = Await.result(aggregate, 2.seconds)

  println(squares.sum)

}


