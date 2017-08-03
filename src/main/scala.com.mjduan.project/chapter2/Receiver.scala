package chapter2

import akka.actor.{Actor, ActorLogging, Props}

/**
  * Hans 2017-08-03 15:03
  */
class Receiver extends Actor with ActorLogging {

    override def receive: Receive = {
        case s: String => log.info(s"Receiver rec '$s' from ${sender()}")
        case i: Int => log.info(s"Receiver rec '$i' from ${sender()}")
        case d: Double => processDouble(d)
        case unknown: Any => log.info(s"Receiver rec unknown msg '$unknown' from ${sender()}")
    }

    def processDouble(d: Double) = {
        log.info(s"Receiver rec '$d' from ${sender()}")
        sender() ! (d + 1.0)
    }
}

object Receiver {
    def props = Props(new Receiver)
}