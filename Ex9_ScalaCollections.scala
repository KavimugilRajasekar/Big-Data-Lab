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
    // This function is pure: it depends only on its input and has no side effects
    def calculateGrade(score: Int): String = {
      if (score >= 90) "A"
      else if (score >= 80) "B"
      else if (score >= 70) "C"
      else "D"
    }

    // 3. Map, Filter, and Fold
    // Filter: Get students with score >= 75
    val topStudents = students.filter(s => s.score >= 75)
    println(s"Students with score >= 75: ${topStudents.map(_.name)}")

    // Map: Transform students to their grades
    val grades = students.map(s => (s.name, calculateGrade(s.score)))
    println(s"Student Grades: $grades")

    // Fold: Calculate total score of all students
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
