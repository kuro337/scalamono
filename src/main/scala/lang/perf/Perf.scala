package lang.perf

// https://github.com/databricks/scala-style-guide?tab=readme-ov-file#perf

object PerfScala {
  def performance(): Unit = {

    /*
      Traversal and zipWithIndex
      Use while loops instead of for loops or functional transformations (e.g. map, foreach).

      For loops and functional transformations are very slow
      (due to virtual function calls and boxing).

      ## Virtual Function Calls

      In OOP languages like Scala and Java, a virtual function call occurs when:

      A method call is resolved at runtime rather than compile-time.

      This happens because the method being called can be overridden in a subclass,
      and the actual method to call is determined based on the runtime type of the object.
      While this provides flexibility and enables polymorphism,
      it also incurs a runtime cost because the determination of
      which method to call is done dynamically.

      ## Boxing

      Boxing is the process of wrapping a primitive type (e.g., int, double)
      in an object so that it can be treated as an object.

      This is necessary in Java and Scala when you want to use primitive types in
      contexts that require objects, such as collections.

      Unboxing is the reverse process, where the object is
      converted back into a primitive type.

      Both boxing and unboxing introduce overhead because of the need to wrap
      and unwrap values, and they can lead to increased garbage collection pressure.

     */

    val arr = Array(10, 20, 5, 3, 11, 9999, 1, 2, 3, 4, 5)

    val newArr = arr.zipWithIndex.map { case (elem, i) =>
      if (i % 2 == 0) 0 else elem
    }

    // This is a high performance version of the above
    val newArrW = new Array[Int](arr.length)
    var i = 0
    val len = newArrW.length
    while (i < len) {
      newArrW(i) = if (i % 2 == 0) 0 else arr(i)
      i += 1
    }

  }
}
