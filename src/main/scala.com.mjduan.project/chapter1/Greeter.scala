import Greeter.{Greet, WhoToGreet}
import Printer.Greeting
import akka.actor.{Actor, ActorRef, Props}

/**
  * Hans 2017-08-03 14:47
  */
class Greeter(message: String, printerActor: ActorRef) extends Actor {
    var greeting = ""

    override def receive: Receive = {
        case WhoToGreet(who) => greeting = s"message,$who"
        case Greet => printerActor ! Greeting(greeting)
    }
}

object Greeter {
    def props(message: String, printerActor: ActorRef): Props = Props(new Greeter(message, printerActor))

    case class WhoToGreet(who: String)

    case object Greet

}