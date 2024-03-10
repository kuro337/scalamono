package lang.oop

object AbstractUsage {

  /* Using Composition to Achieve Interface Driven Design */

  abstract class InterfaceA {
    def fly(): Unit
  }

  abstract class InterfaceB {
    def eject(): Unit
  }

  class ImplA extends InterfaceA {
    def fly() = println("Flying")
  }

  class ImplB extends InterfaceB {
    def eject() = println("Ejecting")
  }

  class Concrete(a: InterfaceA, b: InterfaceB) {
    def start(): Unit = {
      a.fly()
      b.eject()
    }
  }

  /* Using Abstract Class and Traits Together using extends and with */

  abstract class Vehicle(val name: String) {
    def drive(): Unit
  }

  trait Electric {
    val batteryLife: Int
    def recharge(): Unit = println("Recharging")
  }

  class ElectricCar(name: String, override val batteryLife: Int)
      extends Vehicle(name)
      with Electric {
    override def drive(): Unit = println(s"$name is driving")
    override def recharge(): Unit = super.recharge()
  }

  trait Drivable {

    val maxSpeed: Int // Abstract field

    val vehicleType: String = "Unknown" // Concrete field

    def accelerate(speed: Int): Unit // Abstract method

    // Concrete method

    def decelerate(amount: Int): Unit = println(s"Decelerating by $amount")
  }

}
