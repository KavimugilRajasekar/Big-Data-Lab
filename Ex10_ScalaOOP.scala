abstract class Animal {

  // Abstract method
  def sound(): Unit

  // Concrete method
  def sleep(): Unit = {
    println("Animal is sleeping")
  }
}


// Trait 1
trait Flyable {

  def fly(): Unit = {
    println("Animal is flying")
  }
}


// Trait 2
trait Swimmable {

  def swim(): Unit = {
    println("Animal is swimming")
  }
}


// Dog implements abstract method
class Dog extends Animal {

  override def sound(): Unit = {
    println("Dog says Woof")
  }
}


// Duck extends abstract class and mixes two traits
class Duck extends Animal with Flyable with Swimmable {

  override def sound(): Unit = {
    println("Duck says Quack")
  }
}


// Main program
object Ex10_ScalaOOP {

  def main(args: Array[String]): Unit = {

    // Dog object
    val dog = new Dog()

    println("---- DOG ----")

    dog.sound()
    dog.sleep()


    // Duck object
    val duck = new Duck()

    println("\n---- DUCK ----")

    duck.sound()
    duck.sleep()
    duck.fly()
    duck.swim()
  }
}