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
