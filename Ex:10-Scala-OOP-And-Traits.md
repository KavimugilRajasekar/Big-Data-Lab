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
object Ex10_ScalaOOP {
  def main(args: Array[String]): Unit = {
    println("--- Exercise 10: Scala OOP and Traits ---")

    val creditCard = new CreditCardPayment("1234-5678-9012", "John Doe")
    val paypal = new PayPalPayment("john.doe@example.com")
    val cash = new CashPayment()

    val payments: List[PaymentMethod] = List(creditCard, paypal, cash)

    payments.foreach { p =>
      println(s"Processing payment for ${pgetClass(p)}...")
      println(p.processPayment(100.0))

      // Check if payment method is refundable using pattern matching
      p match {
        case r: Refundable => println(r.refund(20.0))
        case _ => println("This payment method is not refundable.")
      }

      // Check if payment method is taxable
      p match {
        case t: Taxable => println(s"Tax applied: ${t.applyTax(100.0)}")
        case _ => println("No tax applicable.")
      }
      println("-" * 30)
    }
  }

  def pgetClass(p: PaymentMethod): String = p.getClass.getSimpleName

  // 1. Define Trait with Abstract Method
  trait PaymentMethod {
    def processPayment(amount: Double): String
  }

  // 2. Mix-in Traits
  trait Refundable {
    def refund(amount: Double): String = s"Refunded amount: $$ $amount successfully."
  }

  trait Taxable {
    def applyTax(amount: Double): Double = amount * 1.15 // 15% tax
  }

  // 3. Concrete Implementations
  class CreditCardPayment(cardNumber: String, holder: String) extends PaymentMethod with Refundable with Taxable {
    override def processPayment(amount: Double): String =
      s"Processed $$ $amount via Credit Card ($cardNumber) for $holder."
  }

  class PayPalPayment(email: String) extends PaymentMethod with Refundable {
    override def processPayment(amount: Double): String =
      s"Processed $$ $amount via PayPal ($email)."
  }

  class CashPayment() extends PaymentMethod {
    override def processPayment(amount: Double): String =
      s"Processed $$ $amount via Cash."
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
