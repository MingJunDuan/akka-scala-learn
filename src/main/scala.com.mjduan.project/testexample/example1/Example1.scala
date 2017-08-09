package testexample.example1

import java.util.concurrent.TimeUnit

import akka.actor.Actor


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Hans 2017-08-09 13:37
  */
private[example1] class Example1 extends Actor {

    override def receive: Receive = {
        case s: String => println(s)
        case 5 => println(5)
        case 9 => {
            println("process9()")
            context.system.scheduler.scheduleOnce(Duration.apply(500, TimeUnit.MILLISECONDS), self, "Hello")
        }
    }

    def process9() = {

    }
}