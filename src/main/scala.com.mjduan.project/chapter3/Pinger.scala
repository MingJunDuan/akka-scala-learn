package chapter3

import akka.actor.{Actor, ActorLogging, PoisonPill}

/**
  * Hans 2017-08-04 09:47
  */
private[chapter3] class Pinger extends Actor with ActorLogging {
    private var countDown = 100

    override def receive: Receive = {
        case Pong => processPong()
        case unknown:Any => log.warning("Rec unknown message")
    }

    def processPong() = {
        log.info(s"Pinger rec ponger, countDown=$countDown")
        if (countDown > 0) {
            countDown -= 1
            sender() ! Ping
        } else {
            sender() ! Ping
            self ! PoisonPill
        }
    }
}

case object Ping

case object Pong