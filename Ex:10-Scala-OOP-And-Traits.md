# Exercise 10 --- Scala: Object-Oriented Programming and Traits

## Aim

Implement an Object-Oriented design in Scala using abstract methods, concrete class implementations, and the mixing of traits to achieve multiple inheritance-like behavior.

## Environment

``` text
OS       : Ubuntu 24.04 LTS
Java     : OpenJDK 8
Scala    : Scala 2.11+ / 3.x
Tooling  : scala-cli / sbt
Concepts : Traits, Mix-ins, Abstract Methods, Concrete Classes, Pattern Matching
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
nano Ex10_ScalaOOP.scala
```

Enter the following implementation:

``` scala
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
```

------------------------------------------------------------------------

## 2. Run the Program

### Execution
``` bash
scala Ex10_ScalaOOP.scala
```


## 3. Verify the Output

Check for the following:
- **Polymorphism**: Ensure all payment types are processed in the `List[PaymentMethod]`.
- **Mix-in traits**:
    - `CreditCardPayment` should be both Refundable and Taxable.
    - `PayPalPayment` should be Refundable but not Taxable.
    - `CashPayment` should be neither.

------------------------------------------------------------------------

# Common Errors

## Diamond Problem
When mixing multiple traits that implement the same method, Scala resolves this using **Linearization**.
**Solution**: Use `super` carefully and understand the order of traits declared in the `extends ... with ...` clause.

## Type Casting
Trying to call a trait-specific method (like `refund`) on a general `PaymentMethod` reference will cause a compilation error.
**Solution**: Use pattern matching (`p match { case r: Refundable => ... }`) to safely cast and check for trait membership at runtime.

------------------------------------------------------------------------

# Practical Exam Short Version

``` bash
# 1. Create file
nano Ex10_ScalaOOP.scala

# 2. Run file
scala Ex10_ScalaOOP.scala

# 3. Verification
# - Check that CreditCard payment is both Refundable and Taxable
# - Check that PayPal payment is only Refundable
# - Check that Cash payment is neither
```

------------------------------------------------------------------------

# Result

A modular payment system was successfully implemented in Scala. The use of traits allowed for flexible behavior composition, enabling different payment methods to share common functionality (like refundability) without rigid class hierarchies. This architecture demonstrates the power of Scala's mix-in composition and type system in creating scalable and maintainable software.
