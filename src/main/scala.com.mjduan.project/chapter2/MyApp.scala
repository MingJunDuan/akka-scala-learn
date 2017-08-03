package chapter2

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

/**
  * Hans 2017-08-03 15:10
  */
object MyApp extends App {

    val system = ActorSystem("actorSystem")
    val sender = system.actorOf(Props(new Sender), "sender")
    val receiver = system.actorOf(Receiver.props, "receiver")
    receiver.tell("你好", sender)
    receiver.tell(123, sender)
    receiver.tell(1.0, sender)

    Thread.sleep(1000)
    system.terminate()
}

class Sender extends Actor with ActorLogging {

    override def receive: Receive = {
        case d: Double => processDouble(d)
        case unknown: Any => processUnknown(unknown)
    }

    def processDouble(d: Double): Unit = {
        log.info(s"Sender rec '$d' from '${sender()}'")
    }

    def processUnknown(unknown: Any): Unit = {
        log.info(s"Sender rec unknown msg '$unknown' from ${sender()}")
    }
}
