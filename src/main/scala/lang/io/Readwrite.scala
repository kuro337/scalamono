package lang.io

import scala.io.StdIn._

object StdIO {

  /* Reads first 2 lines and maps to Ints */
  def readLinesAndSum(): Int = {
    scala.io.Source.stdin.getLines().take(2).map { _.toInt }.sum
  }

  def getLine(): Unit = {

    println("Enter Text")
    val inputLine = readLine()
    println(s"You entered: $inputLine")

  }

  def getLines(): Unit = {
    var line: Option[String] = None
    println("Enter Lines:")
    while ({
      line = Option(scala.io.StdIn.readLine())
      line.isDefined && line.get.nonEmpty
    }) {
      println(s"Read: ${line.get}")
    }
  }

  /*

{“k1”: 1, “k2”: {“k3”: [“a”, “b”, “c”]}, “lookup_key”: {“a”: 1, “b”:
[2]}}

Expected output: {“a”: 1, “b”: [2]}.

   */

  def parseJson(fullJson: String, lookupKey: String): String = {

    val lk = """"lookup_key":"""

    val bkLen = lk.length

    val buf = new scala.collection.mutable.StringBuilder
    val content = new scala.collection.mutable.StringBuilder
    var op = 0
    var cl = 0
    var kf = false
    var r = false

    var currPos = 0
    var ws = 0
    fullJson.foreach { c =>
      {
        if (!r) {

          if (c != ' ') {
            kf match {
              case false => {
                if (currPos < bkLen && c == lk.charAt(currPos)) {
                  buf.append(c)
                  currPos += 1
                } else {
                  buf.clear()
                  currPos = 0
                }
              }
              case true => {

                c match {
                  case '{' => {
                    op += 1
                    content.append(c)

                  }
                  case '}' => {
                    cl += 1
                    content.append(c)

                    if (cl > op) {
                      println(
                        s"Invalid JSON found,${content.toString}. Resetting "
                      )
                      kf = false
                      content.clear()
                      buf.clear()
                    } else if (cl == op) {
                      println(s"Key Value Found! ${content.toString()}")
                      r = true

                    }
                  }
                  case _ => content.append(c)

                }
              }
            }

          } else {
            ws += 1
          }

          if (buf.size == bkLen) {
            println(
              s"Lookup Key Found:${buf.toString}. Reading Content into Buffer"
            )
            buf.clear()
            kf = true
          }

        } else println("Value already found")
      }
    }

    "JSON"
  }

}
