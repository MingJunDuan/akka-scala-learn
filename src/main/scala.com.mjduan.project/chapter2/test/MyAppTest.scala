package chapter2.test

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import chapter2.Receiver
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.duration.Duration

/**
  * Hans 2017-08-03 15:27
  */
class MyAppTest(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

    def this() = this(ActorSystem("akkaSystem"))

    override protected def afterAll(): Unit = shutdown(system)

    "Sender Actor" should "receive 2.0" in {
        val testProbe = TestProbe()
        val receiverActor = system.actorOf(Receiver.props)
        receiverActor.tell(1.0, testProbe.ref)
        testProbe.expectMsg(Duration.apply(1, TimeUnit.SECONDS), 2.0)
    }

    "Sender Actor" should "receive 3.0" in {
        val testProbe = TestProbe()
        val receiverActor = system.actorOf(Receiver.props)
        receiverActor.tell(2.0, testProbe.ref)
        testProbe.expectMsg(Duration.apply(1, TimeUnit.SECONDS), 3.0)
    }

    "Sender Actor" should "receive 4.0" in {
        val testProbe = TestProbe()
        val receiverActor = system.actorOf(Receiver.props)
        receiverActor.tell(3.0, testProbe.ref)
        testProbe.expectMsg(Duration.apply(1, TimeUnit.SECONDS), 4.0)
    }

}
