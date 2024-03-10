package lang.datastructures.list
import scala.collection.mutable.Buffer

object ListApp {

  // 22731434672

  def repeatListElemNTimes(repeat: Int, arr: List[Int]): List[Int] =
    arr.flatMap(num => List.fill(repeat)(num))

  def scalaListUsage(): Unit = {
    println("""Scala Lists:
    
    Default Immutable Lists:
    
    val s_list = List[Int]()

    val prepend = 1 :: s_list 
    val appendedList = s_list :+ 1 

    Combining Lists 

    val c1 = s_list ++  anotherList 
    val c2 = s_list ::: anotherList 



    val squaredList = l.map(x => x * x)
    val evenList =    l.filter(x => x % 2 == 0)

    val sum = l.foldLeft(0)(_ + _)

    Reduce : similar to foldLeft but uses the First Element

    val product = initializedList.reduce(_ * _)


    val nestedList = List(List(1, 2), List(3, 4))
    val flat = nestedList.flatMap(x => x)


    Mutable Lists (Buffer)

    import scala.collection.mutable.Buffer

    val buf = Buffer[Int]()


    buf += 1           // Appending
    buf.append(1,2,3)  // Appending multiple values 
    buf.prepend(0)     // Prepend

    buf.insert(1, 2)   // Inserts 2 at index 1
    buf -= 1           // removes 1st occurence of an Element

    buf.remove(1)      // Removes element at index 1
    buf(0) = 10        // Update the first element to 10

    buf.clear()

    val immutableList = buf.toList

    - Map + Filter

    Returns a new Buffer, buf remains unchanged

    val mappedBuffer = buf.map(_ * 2)

    Returns a new Buffer with even numbers

    val filteredBuffer = buf.filter(_ % 2 == 0)
    
    """)
  }
  def listUsage(): Unit = {

    val casematching = List(1, 2, 3, 4, 5)
    casematching match {
      case l :: tail =>
        println(s"Head $l Tail of List : ${tail.mkString(",")}")
      case _ => println("No head and Tail ")
    }

    // Int       :: List[Int]  -> List[Int]

    // List[Int] :+ Int        -> List[Int]

    val tuple_list: List[(Int, Int)] = List((1, 2))

    val s_list = List[Int]()

    val newList = 1 :: s_list // efficient as doesnt require Copying
    val appendedList = s_list :+ 1 // less efficient - needs to copy full list

    val anotherList = List(2, 3)

    /* ::: is List-Specific whereas ++ is generic over Collections
       So use ::: when we know we want to concat lists
     */

    val combinedList2 = s_list ::: anotherList // Using :::
    val combinedList = s_list ++ anotherList // Using ++

    val initializedList = List(1, 2, 3)

    val squaredList = initializedList.map(x => x * x)
    val evenList = initializedList.filter(x => x % 2 == 0)

    val sum = initializedList.foldLeft(0)(_ + _)

    // similar to foldLeft but C[0] as 1st elem
    val product = initializedList.reduce(_ * _)

    val nestedList = List(List(1, 2), List(3, 4))
    val flat = nestedList.flatMap(x => x)

  }

  def listMutableUsage(): Unit = {

    // import scala.collection.mutable.Buffer

    val mBuffer = Buffer[Int]()

    mBuffer += 1 // Append
    mBuffer.append(4, 5, 6) // multiple appends
    mBuffer.prepend(0)
    mBuffer.insert(1, 2) // Inserts 2 at index 1
    mBuffer -= 1 // removes 1st occurence of an Element

    mBuffer.remove(1) // Removes element at index 1
    mBuffer(0) = 10 // Update the first element to 10
    mBuffer.clear()
    val immutableList = mBuffer.toList

    // Map + Filter

    // Returns a new Buffer, mBuffer remains unchanged

    val mappedBuffer = mBuffer.map(_ * 2)

    // Returns a new Buffer with even numbers

    val filteredBuffer = mBuffer.filter(_ % 2 == 0)

  }
}
