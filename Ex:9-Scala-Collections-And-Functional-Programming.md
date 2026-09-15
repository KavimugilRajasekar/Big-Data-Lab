# Exercise 9 --- Scala: Collections and Functional Programming

## Aim

Implement and manipulate fundamental Scala collections (Lists, Sets, Maps) and apply functional programming principles such as pure functions, immutability, higher-order functions (map, filter, fold), and recursion.

## Environment

``` text
OS       : Ubuntu 24.04 LTS
Java     : OpenJDK 8
Scala    : Scala 2.11+ / 3.x
Tooling  : scala-cli / sbt
Concepts : Immutability, Pure Functions, Higher-Order Functions, Recursion
```

------------------------------------------------------------------------

## Installation

If Scala is not already installed on your system, run the following commands:
``` bash
sudo apt-get update
sudo apt-get install -y scala
```

------------------------------------------------------------------------

## 1. Create the Scala File

``` bash
nano Ex9_ScalaCollections.scala
```

Enter the following implementation:

``` scala
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
```

------------------------------------------------------------------------

## 2. Run the Program

### Execution
``` bash
scala Ex9_ScalaCollections.scala
```

### ⚠️ REPL Troubleshooting
If you try to enter the interactive shell by typing `scala` and encounter a `NoClassDefFoundError: org/fusesource/jansi/AnsiOutputStream`, this is a known issue with the `apt` version of Scala on Ubuntu 24.04 due to a Jansi version mismatch.

**Solution (The Easy Way):**
You can launch the shell by explicitly pointing to the compatible Jansi library:
``` bash
scala -cp /usr/share/java/jansi1.jar
```

**Making it Permanent (The Wrapper):**
To avoid typing the long command every time, create a shortcut:
``` bash
# 1. Create the wrapper script
echo '#!/bin/bash' > ~/scala-repl
echo 'scala -cp /usr/share/java/jansi1.jar' >> ~/scala-repl

# 2. Make it executable
chmod +x ~/scala-repl

# 3. (Optional) Move to bin for global access
sudo mv ~/scala-repl /usr/local/bin/scala-repl
```
Now, you can simply type `scala-repl` to start the interactive shell.

------------------------------------------------------------------------

## 3. Verify the Output

Check for the following:
- **Unique Subjects**: "Math" should appear only once.
- **Filtering**: Only Alice and Charlie should be listed as top students.
- **Aggregation**: Total score should be 312.
- **Recursion**: Recursive sum should match the foldLeft total.

------------------------------------------------------------------------

# Common Errors

## Immutable vs Mutable Collections
Using `List` or `Map` creates immutable collections. Attempting to modify them directly using mutation methods will fail.
**Solution**: Create new collections or use `scala.collection.mutable` if mutation is required.

## StackOverflowError in Recursion
Deep recursion on large lists can cause a `StackOverflowError`.
**Solution**: Use the `@tailrec` annotation to optimize the function into a loop.

------------------------------------------------------------------------

# Practical Exam Short Version

``` bash
# 1. Create file
nano Ex9_ScalaCollections.scala

# 2. Run file
scala Ex9_ScalaCollections.scala

# 3. Verification
# - Check Set for duplicates
# - Check filter for score >= 75
# - Check foldLeft and recursion results
```

------------------------------------------------------------------------

# Result

The implementation successfully demonstrated the use of Scala's core collections and functional programming paradigms. By utilizing pure functions and immutable data structures, the code achieves predictability and thread-safety. The application of `map`, `filter`, and `fold` showcases how to process data concisely, while recursion provides a powerful alternative to iterative loops.
