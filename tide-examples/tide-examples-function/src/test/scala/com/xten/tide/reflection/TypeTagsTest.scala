package com.xten.tide.reflection

import scala.reflect.runtime.{universe => ru}


object TypeTagsTest {



  def main(args: Array[String]): Unit = {

    val user =new User

    val theType = getTypeTag(user).tpe

    println(theType)


  }

  def getTypeTag[T : ru.TypeTag](obj :T) = ru.typeTag[T]

}



class User {

  private var id:Int = _
  private var name :String = _

}

case class Person(name : String)


