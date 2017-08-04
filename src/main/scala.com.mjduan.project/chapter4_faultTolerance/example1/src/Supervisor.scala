package chapter4_faultTolerance.example1.src

import java.util.concurrent.TimeUnit

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, OneForOneStrategy, Props}

import scala.concurrent.duration.Duration

/**
  * Hans 2017-08-04 13:20
  */
class Supervisor extends Actor{

    override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10,withinTimeRange = Duration.apply(1,TimeUnit.MINUTES)){
        case _:ArithmeticException => Resume
        case _:NullPointerException=> Restart
        case _:IllegalArgumentException=>Stop
            //往上抛
        case _:Exception=>Escalate
    }

    override def receive: Receive = {
        case p:Props=>sender()!context.actorOf(p)
    }
}
