object Ex9_ScalaCollections {

  // Pure function
  def square(x: Int): Int = {
    x * x
  }

  // Recursive function
  def factorial(n: Int): Int = {
    if (n <= 1)
      1
    else
      n * factorial(n - 1)
  }

  def main(args: Array[String]): Unit = {

    // -------------------------
    // LIST
    // -------------------------

    val numbers = List(1, 2, 3, 4, 5)

    println("Original List: " + numbers)

    val doubled = numbers.map(_ * 2)

    println("Doubled List: " + doubled)


    // -------------------------
    // FILTER
    // -------------------------

    val evenNumbers = numbers.filter(_ % 2 == 0)

    println("Even Numbers: " + evenNumbers)


    // -------------------------
    // FOLD
    // -------------------------

    val sum = numbers.fold(0)(_ + _)

    println("Sum: " + sum)


    // -------------------------
    // SET
    // -------------------------

    val set1 = Set(1, 2, 3, 4)
    val set2 = Set(3, 4, 5, 6)

    println("Set 1: " + set1)
    println("Set 2: " + set2)

    println("Union: " + (set1 union set2))
    println("Intersection: " + (set1 intersect set2))


    // -------------------------
    // MAP
    // -------------------------

    val students = Map(
      "Kavi" -> 90,
      "Arun" -> 85,
      "Priya" -> 95
    )

    println("Students: " + students)
    println("Kavi's Mark: " + students("Kavi"))


    // -------------------------
    // PURE FUNCTION
    // -------------------------

    println("Square of 5: " + square(5))


    // -------------------------
    // RECURSION
    // -------------------------

    println("Factorial of 5: " + factorial(5))
  }
}