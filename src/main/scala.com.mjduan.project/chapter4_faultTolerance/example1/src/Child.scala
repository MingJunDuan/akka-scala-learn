package chapter4_faultTolerance.example1.src

import akka.actor.Actor

/**
  * Hans 2017-08-04 13:25
  */
class Child extends Actor {
    private var state = 0

    override def receive: Receive = {
        case ex: Exception => throw ex
        case x: Int => state = x
        case "get" => sender() ! state
    }
}
