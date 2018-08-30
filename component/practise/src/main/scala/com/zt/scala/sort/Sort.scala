package com.zt.scala.sort

object Sort {
  def main(args: Array[String]): Unit = {
    println("冒泡排序：")
    var array = Array(5, 2, 4, 7, 8, 3, 3, 52, 45, 5, 11, 5)
    bubbleSort(array)
    array.foreach(e => print(e + " "))

    println("\n选择排序：")
    array = Array(5, 2, 4, 7, 8, 3, 3, 52, 45, 5, 11, 5)
    selectionSort(array)
    array.foreach(e => print(e + " "))

    println("\n插入排序：")
    array = Array(5, 2, 4, 7, 8, 3, 3, 52, 45, 5, 11, 5)
    insertionSort(array)
    array.foreach(e => print(e + " "))

    println("\n希尔排序：")
    array = Array(5, 2, 4, 7, 8, 3, 3, 52, 45, 5, 11, 5)
//    shellSort(array)
    array.foreach(e => print(e + " "))

  }

  //冒泡排序  O(n^2)
  def bubbleSort(array: Array[Int]) = {
    val len = array.length
    for (i <- 0 until len - 1) {
      for (j <- 0 until len - 1 - i) {
        if (array(j) > array(j + 1)) {
          val temp = array(j + 1)
          array(j + 1) = array(j)
          array(j) = temp
        }
      }
    }
  }

  //选择排序 O(n^2)
  def selectionSort(array: Array[Int]) = {
    val len = array.length
    var minIndex: Int = 0
    var temp: Int = 0

    for (i <- 0 until len - 1) {
      minIndex = i
      for (j <- i + 1 until len) {
        if (array(j) < array(minIndex))
          minIndex = j
      }
      temp = array(i)
      array(i) = array(minIndex)
      array(minIndex) = temp
    }
  }

  //插入排序
  def insertionSort(array: Array[Int]) = {
    val len = array.length
    var preIndx = 0
    var current = 0

    for (i <- 1 until len) {
      preIndx = i - 1
      current = array(i)

      while (preIndx >= 0 && array(preIndx) > current) {
        array(preIndx + 1) = array(preIndx)
        preIndx -= 1
      }
      array(preIndx + 1) = current
    }
  }

  //希尔排序
  /* def shellSort(array: Array[Int]): Unit = {
     val len = array.length
     var temp = 0
     var gap: Int = 1
     while (gap < len / 3) {
       gap = gap * 3 + 1
     }


     while (gap>0){
       for (i <- gap until len - 1) {
         temp = array(i)
         var j = i - gap
         while (j > 0 && array(j) > temp) {
           array(j + gap) = array(j)
           j -= gap
         }
         array(j + gap) = temp
       }
       gap = Math.floor(gap/3).toInt
     }
   }*/
  //归并排序
  def mergeSort(array: Array[Int]): Array[Int] = {
    val len = array.length
    if (len < 2)
      array

    var middle = Math.floor(len/2)

  }

}