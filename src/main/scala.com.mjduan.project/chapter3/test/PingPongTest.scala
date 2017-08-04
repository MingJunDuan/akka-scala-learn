package chapter3.test

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

/**
  * Hans 2017-08-04 09:57
  */
class PingPongTest(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

    //构造方法
    def this() = this(ActorSystem("akkaSystem"))

    override protected def afterAll(): Unit = shutdown(system)

    "Sender Actor" should "receive 2.0" in {
        import chapter3.{Pinger, Pong, Ponger}
        val pingerActor = system.actorOf(Props[Pinger], "pingerActor")
        val pongerActor = system.actorOf(Props[Ponger], "pongerActor")
        pingerActor.tell(Pong, pongerActor)
    }

}
