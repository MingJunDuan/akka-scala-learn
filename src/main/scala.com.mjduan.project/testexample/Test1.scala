package testexample

import akka.actor.{Actor, ActorSystem, Props}

/**
  * Hans 2017-08-09 13:40
  */
object Test1 {
    def main(args: Array[String]): Unit = {
        val system = ActorSystem.apply("actorSystem")
        val actor1 = system.actorOf(Props[Example1])
        actor1.tell("msg", Actor.noSender)
        actor1.tell(5, Actor.noSender)
        actor1.tell(9, Actor.noSender)
        Thread.sleep(3000)
        system.terminate()
    }

}
