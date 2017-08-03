import Printer.Greeting
import akka.actor.{Actor, ActorLogging, Props}

/**
  * Hans 2017-08-03 14:52
  */
class Printer extends Actor with ActorLogging {
    override def receive: Receive = {
        case Greeting(greeting) => log.info(s"Printer rec msg '$greeting' from ${sender()}")
    }
}

object Printer {
    def props: Props = Props[Printer]

    case class Greeting(greeting: String)

}

