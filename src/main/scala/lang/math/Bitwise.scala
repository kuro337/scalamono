package lang.math

object Bitwise {
  def bitdoc(): Unit = {
    println("""Bitwise Operators

Exclusive OR (xor) : ^    -> if only one bit is a 1 , returns a 1
1 ^ 0 => 1
1 ^ 1 => 0  

Can be used to toggle between 1 and 0 , x ^ 1 => toggles 1/0

Keeps exclusive 1's and removes if both bits are 1 

  101
^ 011
= 110

Or : |   -> if at least one of the bits is 1 , returns a 1 
Keeps 1's even if both bits are 1
1 | 1 => 1
1 | 0 => 1

  101
| 011
= 111


    """)
  }

  /*

   Checks the Bit at pos 0..32 for each Num in the Array
   If over half the nums have bit at ith pos set
   Then the Majority Element will surely have it

   */

  // Powers of 2 , subtracting 1 shifts all bits to right to 1 : 8 -> 1000 , 7 -> 0111

  def isPowerOfTwo(n: Int): Boolean = n > 0 && ((n & (n - 1)) == 0)

  def WhichNumShowUpOverHalfTheTime(nums: Array[Int]): Int = {
    (0 until 32).foldLeft(0) { case (bit, i) =>
      if (nums.count(n => (n & (1 << i)) != 0) > (nums.size / 2)) bit | (1 << i)
      else bit
    }
  }

  def findingFrequentNum(nums: Array[Int]): Int = {
    (0 until 32).foldLeft(0) { case (acc, i) =>
      nums.foldLeft(0) { case (count, n) =>
        if (bitExistsAtPos(n, i)) count + 1 else count
      } match {
        case c if c > nums.size / 2 => acc | (1 << i)
        case _                      => acc
      }
    }

  }

  def toggleOneZero(num: Int): Int = num ^ 1

  def findUniqueElement(nums: Array[Int]): Int = nums.reduce(_ ^ _)

  // find missing num from 0 to n in arr
  def findMissingConsecutiveNum(arr: Array[Int]): Int = {
    /* Note: a number XOR with 0 doesnt change the number */
    (0 to arr.size).foldLeft(0) { _ ^ _ } ^ (arr.foldLeft(0) { _ ^ _ })
  }

  // If num and i are both 16 bit ints we can store them in a single Int using a bitmask
  def storeNumAndIndexInMask(num: Int, i: Int): Int = {
    (num << 16) | (i)
  }

  // ffff = 16x1 , 16x0 so doing & will cancel out last 16 bits
  def keepFirst16Bits(num: Int): Int = num & 0xffff

  // ffff = 16x1 , 16x0 so doing & will cancel out last 16 bits
  def keepLast16Bits(num: Int): Int = num & 0x0000ffff

  // 55555555 => 01010101010101010101010101010101
  def keepAlternateBitsSkipOne(num: Int): Int = num & 0x55555555

  // AAAAAAAA => 10101010101010101010101010101010
  def keepAlternateBitsKeepZeroth(num: Int): Int = num & 0xaaaaaaaa

  def keepAlternateBitsFromStart(num: Int): Int = num & (0x55555555 << 1)

  // store 2 16 bit ints in a single 32 bit int by checking each bit (n & 0xffff better)
  def keepHalfHalf(a: Int, b: Int): Int = {
    (0 until 32).foldLeft(0) { case (acc, n) =>
      n match {
        case n if n < 16 => if (((a >> n) & 1) == 1) acc | (1 << n) else acc
        case _ => if (((b >> (n - 16)) & 1) == 1) acc | (1 << (n)) else acc
      }
    }
  }

  def decipherBoth16BitIntsFromMask(m: Int): (Int, Int) = {
    (
      (0 until 16).foldLeft(0) { case (acc, n) =>
        if (((m >> n) & 1) == 1) acc | (1 << n) else acc
      },
      (16 until 32).foldLeft(0) { case (acc, n) =>
        if (((m >> n) & 1) == 1) acc | (1 << (n - 16)) else acc
      }
    )
  }

  def bitExistsAtPos(num: Int, pos: Int): Boolean = {
    ((1 << pos) & num) != 0

  }
  def getBitArray(num: Int): Array[Int] = {
    val bits = Array.ofDim[Int](32)
    for (i <- 0 until 32) {
      val b = 1 << i
      if ((b & num) != 0) bits(31 - i) = 1
    }
    bits
  }

  def resultOfBitwiseAndOverRangeInclusive(l: Int, r: Int): Int = {
    @scala.annotation.tailrec
    def helper(l: Int, r: Int, shift: Int = 0): Int = {
      if (l == r) l << shift else helper(l >> 1, r >> 1, shift + 1)
    }
    helper(l, r)
  }

  // Given l , r find the result of & between l and r inclusive (4,10) -> 4 & 5...& 10 = ?
  def findRangeResultCumulativeAnd(left: Int, right: Int): Int = {
    // For a range of nums - we need to find their common prefix and shift it back by the beginning 1s

    // If there is no common 1 at the same pos -> it will be 0
    // 1000 -> 100 -> 10 -> 1 -> 0
    // 0101 ->  10 ->  1 -> 0 -> 0

    // But for
    // 1000 -> 100 -> 10 -> 1 -> 0
    // 1010 -> 101 -> 10 -> match found and we moved 2 Bits so return 10 << 2

    var s = 0
    var l = left
    var r = right

    while (l != r) {
      l = l >> 1
      r = r >> 1
      s += 1
    }

    l << s

  }

}
