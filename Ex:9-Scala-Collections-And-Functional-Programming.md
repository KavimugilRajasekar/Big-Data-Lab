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
  def main(args: Array[String]): Unit = {
    println("--- Exercise 9: Scala Collections & Functional Programming ---")

    // 1. Lists, Sets, and Maps
    val students = List(
      Student("Alice", 85),
      Student("Bob", 72),
      Student("Charlie", 90),
      Student("David", 65)
    )

    val subjects = Set("Math", "Physics", "Chemistry", "Math") // Set removes duplicates
    println(s"Unique Subjects: $subjects")

    val studentScores = Map("Alice" -> 85, "Bob" -> 72, "Charlie" -> 90, "David" -> 65)
    println(s"Student Scores Map: $studentScores")

    // 2. Pure Functions & Immutability
    def calculateGrade(score: Int): String = {
      if (score >= 90) "A"
      else if (score >= 80) "B"
      else if (score >= 70) "C"
      else "D"
    }

    // 3. Map, Filter, and Fold
    val topStudents = students.filter(s => s.score >= 75)
    println(s"Students with score >= 75: ${topStudents.map(_.name)}")

    val grades = students.map(s => (s.name, calculateGrade(s.score)))
    println(s"Student Grades: $grades")

    val totalScore = students.foldLeft(0)((acc, s) => acc + s.score)
    println(s"Total Score of all students: $totalScore")

    // 4. Recursion
    def sumList(list: List[Int]): Int = {
      if (list.isEmpty) 0
      else list.head + sumList(list.tail)
    }

    val scoresOnly = List(85, 72, 90, 65)
    println(s"Sum of scores using recursion: ${sumList(scoresOnly)}")

    println("\nExecution completed successfully.")
  }

  case class Student(name: String, score: Int)
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
