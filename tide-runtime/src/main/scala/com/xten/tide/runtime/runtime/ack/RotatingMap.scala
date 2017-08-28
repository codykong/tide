package com.xten.tide.runtime.runtime.ack

import scala.collection.mutable


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/6/22 
  */

object RotatingMap{
  //this default ensures things expire at most 50% past the expiration time
  private val DEFAULT_NUM_BUCKETS = 3;

  def apply[K,V](bucketNums: Int,callback: ExpiredCallback[K,V]): RotatingMap[K,V] = new RotatingMap[K,V](bucketNums,callback)

  def apply[K,V](callback: ExpiredCallback[K,V]): RotatingMap[K,V] = new RotatingMap[K,V](DEFAULT_NUM_BUCKETS,callback)

  def main(args: Array[String]): Unit = {

    val callback  = new ExpiredCallback[String,String] {
      override def expire(key: String, value: String): Unit = {
        println(s"key is ${key},value is ${value}")
      }
    }

    val rotatingMap = RotatingMap.apply(2,callback)

    for (i <- 1 to 2){
      rotatingMap.put(i.toString,i.toString)
    }

    rotatingMap.rotate()

    for (i <- 3 to 4){
      rotatingMap.put(i.toString,i.toString)
    }
    rotatingMap.rotate()

  }

}

trait ExpiredCallback[K,V] {
  def expire(key : K,value :V)
}

class RotatingMap[K,V](private val bucketNums : Int,private val callback: ExpiredCallback[K,V]) {

  require(bucketNums >1,"bucketNums must be >1")

  private var _buckets: mutable.MutableList[mutable.HashMap[K,V]] = new mutable.MutableList[mutable.HashMap[K, V]]
  initBuckets()

  // 初始化桶里的内容
  private def initBuckets(): Unit ={
    for (i <- 1 to bucketNums){
      _buckets.+=(mutable.HashMap.empty[K,V])
    }
  }

  /**
    * 对超时的信息进行处理
    * @return
    */
  def rotate():Map[K,V] = {
    val dead = _buckets.tail.get(0).get.toMap[K,V]
    _buckets = _buckets.diff(_buckets.tail)


    _buckets =  mutable.HashMap.empty[K,V] +: _buckets
    if (callback!=null){

      dead.foreach( (e:(K,V)) => callback.expire(e._1,e._2))
    }

    dead
  }

  /**
    * 将最新的值放入最新的桶内，删除其他桶内的值
    * @param key
    * @param value
    */
  def put(key :K ,value:V): Unit ={

    val iterator =  _buckets.iterator
    var bucket = iterator.next()
    bucket.put(key,value)

    while (iterator.hasNext){
      bucket = iterator.next()
      bucket.remove(key)
    }
  }

  def get(key:K) : Option[V] ={
    for (bucket <- _buckets){
      if (bucket.contains(key)){
        return bucket.get(key)
      }
    }
    return None
  }

  def remove(key:K):Option[V] = {
    for (bucket <- _buckets){
      if (bucket.contains(key)){
        return bucket.remove(key)
      }
    }

    return None
  }







}
