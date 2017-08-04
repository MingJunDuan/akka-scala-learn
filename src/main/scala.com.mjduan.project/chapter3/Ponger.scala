package chapter3

import akka.actor.{Actor, ActorLogging}

/**
  * Hans 2017-08-04 09:48
  */
private[chapter3] class Ponger extends Actor with ActorLogging {

    override def receive: Receive = {
        case Ping => processPing()
    }

    def processPing() = {
        log.info("Ponger rec ping")
        sender() ! Pong
    }
}
