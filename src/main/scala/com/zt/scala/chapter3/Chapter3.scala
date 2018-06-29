package com.zt.scala.chapter3

object Chapter3 extends App {


  def isEven(n: Int) = (n % 2) == 0

  List(1, 2, 3, 4) filter isEven foreach println
  //list 头部添加
  //
  val list = List('a', 'b', 'c')
  'a' :: list foreach (x => print(x + " "))
  println()
  list.+:('a') foreach (x => print(x + " "))
  println()
  list.::('a') foreach (x => print(x + " "))
  println()
  //list 追加
  list.:+('d') foreach (x => print(x + " "))
  println()

  list ::: Nil foreach (x => print(x + " "))
  println()
  val dogBreeds = List("Doberman", "Yorkshire Terrier", "Dachshund", "Scottish Terrier", "Great Dane", "Portuguese Water Dog")
  for (breed <- dogBreeds)
    println(breed)

  for (breed <- dogBreeds
       if breed.contains("Terrier")
  ) println(breed)

  for (breed <- dogBreeds
       if breed.contains("Terrier")
       if !breed.startsWith("Yorkshire")
  ) println(breed)

  for (breed <- dogBreeds
       if breed.contains("Terrier") && !breed.startsWith("Yorkshire")
  ) println(breed)

  val filteredBreeds = for {
    breed <- dogBreeds
    if breed.contains("Terrier") && !breed.startsWith("Yorkshire")
  } yield breed

  println()
  for (breed <- filteredBreeds)
    println(breed)

  for {
    breed <- dogBreeds
    upcasedBreed = breed.toUpperCase()
  } println(upcasedBreed)


  println()
  val dogBreeds2 = List(Some("Doberman"), None, Some("Yorkshire Terrier"),
    Some("Dachshund"), None, Some("Scottish Terrier"),
    None, Some("Great Dane"), Some("Portuguese Water Dog"))

  for {
    breedOption <- dogBreeds2
    breed <- breedOption
    upcasedBreed = breed.toUpperCase
  } println(upcasedBreed)

  for {
    Some(breed) <- dogBreeds2
    upcasedBreed = breed.toUpperCase()
  } println(upcasedBreed)


}

