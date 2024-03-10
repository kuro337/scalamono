package lang.utils

import scala.collection.immutable.List
import scala.collection.mutable.ArrayBuffer
import scala.util.{Try, Success, Failure}

object ImportsScala {
  def importantImports(): Unit = {
    println("""Importing Useful Functionality:

Scala Collections 

import scala.collection.immutable.List
import scala.collection.mutable.ArrayBuffer

For exception handling import from scala.util 

import scala.util.{Try, Success, Failure}


    """)
  }
}
