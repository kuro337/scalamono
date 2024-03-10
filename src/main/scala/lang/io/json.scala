package lang.io

import scala.collection.mutable

object JSON {
  def jsonParsing(): Unit = {}

  /*
JSON Rules

Types => {} , [] , String , Number , Null , Boolean

Object {} -> Key must be String, Value can be any JSON Type

Arrays    -> Can contain any JSON Type

Strings   -> Any Unicode Value enclosed in "" is Valid ,
             Quotes " must be Escaped with \ , \ must be escaped with \\

Numbers   -> Int or Floats without Quotes
             Extra Leading 0 invalid -> 03.04 invalid , 0.04 valid

Booleans  -> Literals with true / false

Null      -> Literal null

Whitespace: These Escapes are ignored by the parser if outside a String

- Valid outside a K/V in JSON : \t , \n , \r

- Windows    \r  -> \r\n used for newline
- MacOS/Unix \r  -> \n used for newline , \r not common
- IDE/Editor \r  -> Causes the cursor to go to start of line overwriting currline

Within a String - All Control Chars are Valid

   */

/// Represents the Inner Parsing State of Values
  sealed trait Parsing

  case object Number extends Parsing
  case object Array extends Parsing
  case object Object extends Parsing
  case object String extends Parsing
  case object Boolean extends Parsing
  case object Null extends Parsing
  case object Whitespace extends Parsing

  // enum Parsing {
  //   case Number,
  //     Object,
  //     Array,
  //     String,
  //     Boolean,
  //     Null,
  //     Whitespace
  // }

// Represents if the Parser is only reading Data or Looking for a Value too

  sealed trait ParserState
  case object Indexing extends ParserState
  case object KeyFound extends ParserState
  case object ValueFound extends ParserState
  case object ReadOnly extends ParserState

  // enum ParserState {
  //   case Indexing,
  //     KeyFound,
  //     ValueFound,
  //     ReadOnly
  // }

  class JsonP(var content: String = "") {

    var lookupKey: Option[String] = None

    var lk = ""

    def replaceContent(body: String): Unit = this.content = body
    def streamContent(data: String): Unit = this.content ++= data
    def streamChar(ch: Char): Unit = this.content += ch

    def setLookupkey(key: String): Unit = {

      // this.lk = s"\"$key\":"

      this.lk = s"""\"$key\":"""

      this.currState = Indexing
    }

    private def getCharIfNotEscape(ch: Char): Option[Char] = {
      ch match {
        case ' ' | '\r' | '\t' | '\n' => None
        case _                        => Some(ch)
      }
    }

    // String Parsing - Strings accept anything - assumes first " is populated - return True if done parsing
    private def handleString(ch: Char): Boolean = {
      currVal.append(ch)
      ch match {
        case '\"' => true
        case _    => false
      }
    }

    // Handles the Key : need it to parse Keys or Nested Keys within Objects
    private def handleKey(ch: Char): Boolean = {
      true
    }

    // Handles Arrays: Arrays will have any String/Number/Object/Array Type within
    // We need to handle this recursively - and initially set a Type for Arr Objects
    private def handleArray(ch: Char): Boolean = {
      true
    }

    // Objects similar to Arrays will have Nested Logic - detect ending of Object by keeping track of Opening/Closing
    private def handleObject(ch: Char): Boolean = {
      true
    }

    // Handles Boolean Parsing - assumes first t or f is already populated
    private def handleBoolean(ch: Char): Boolean = {
      true
    }
    // Handle Null type values and Return True when null complete
    private def handleNull(ch: Char): Boolean = {
      val c = currVal.toString
      ch match {
        case 'u' => {
          if (c != "n") {
            println("Invalid Null Value")
            false
          } else {

            currVal.append(ch)
            false
          }
        }
        case 'l' => {
          c match {
            case "nul" => {
              currVal.append(ch)
              true
            }
            case "nu" => {
              currVal.append(ch)
              false
            }
            case _ => {
              println("invalid null parsing value")
              false
            }
          }

        }
        case _ => {
          println("INVALID NULL VALUE detected. Parsing Failed")
          false
        }
      }
    }
    // Number Parsing - assumes the first char is set Already -> Return True if Number is Parsed
    // Account for Negative Numbers , Exponents , etc.
    private def handleNumber(ch: Char): Boolean = {
      ch match {
        case '}' | ',' => true

        case '.' => {
          if (currVal.toString.contains('.')) {
            println("Invalid Number")
            false
          } else {
            currVal.append(ch)
            false
          }
        }
        case '0' => {
          if (currVal.toString == "0") {
            println("Invalid Number")
            false
          } else {
            currVal.append(ch)
            false
          }
        }
        case '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' => {
          currVal.append(ch)
          false
        }
      }
    }

    private var currKey = new mutable.StringBuilder
    private var currVal = new mutable.StringBuilder
    private var pos = 0

    private var parseState: Parsing = Whitespace
    private var currState: ParserState = ReadOnly

    private def parseCurrChar(c: Char): Unit = {

      currState match {
        case Indexing => {
          c match {
            case ' ' | '\r' | '\t' | '\n' => println("Ignoring")
            case c if pos < lk.size && c == lk(pos) => {
              currKey.append(c)
              pos += 1
              if (pos > lk.size) {
                currState = KeyFound
              }
            }
            case _ => {
              currKey.clear()
              pos = 0
            }
          }
        }
        case KeyFound => {
          parseState match {
            case Whitespace => {
              getCharIfNotEscape(c) match {
                case Some(ch) => {
                  currVal.append(ch)
                  ch match {
                    case '['       => parseState = Array
                    case '{'       => parseState = Object
                    case '\"'      => parseState = String
                    case 't' | 'f' => parseState = Boolean
                    case 'n'       => parseState = Null
                    case _         => parseState = Number
                  }
                }
                case _ => println("ignoring")
              }
            }

            case _ => println("unknown")
          }
        }
        case _ => println("x")
      }

    }
  }
}
