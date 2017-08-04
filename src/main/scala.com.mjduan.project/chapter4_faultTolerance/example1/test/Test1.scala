package chapter4_faultTolerance.example1.test

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props, Terminated}
import akka.testkit.{TestKit, TestProbe}
import chapter4_faultTolerance.example1.src.{Child, Supervisor, Supervisor2}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.duration.Duration

/**
  * Hans 2017-08-04 13:29
  */
class Test1(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

    //变量system在TestKit里面
    override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)

    def this()=this(ActorSystem("System",ConfigFactory.parseString(
        """
          akka{
            loggers=["akka.testkit.TestEventListener"]
            loglevel="info"
          }
        """.stripMargin)))

    "Supervisor" must "apply the chose strategy for its child" in{
        val probe = new TestProbe(system)
        val supervisor = system.actorOf(Props[Supervisor])
        supervisor.tell(Props[Child],probe.ref)
        var childActor = probe.expectMsgType[ActorRef](Duration.apply(1,TimeUnit.SECONDS))
        println("test get 42")
        childActor!42
        childActor.tell("get",probe.ref)
        probe.expectMsg[Int](Duration.apply(1,TimeUnit.SECONDS),42)
        println("test resume")
        childActor!new ArithmeticException()
        childActor.tell("get",probe.ref)
        probe.expectMsg[Int](Duration.apply(1,TimeUnit.SECONDS),42)
        println("test restart")
        childActor!new NullPointerException
        childActor.tell("get",probe.ref)
        probe.expectMsg[Int](Duration.apply(1,TimeUnit.SECONDS),0)
        println("test stop")
        probe.watch(childActor)
        childActor!new IllegalArgumentException
        probe.expectMsgPF(){case t:Terminated=>{}}
        probe.unwatch(childActor)

        //test Escalate...默认情况下，top-level的Actor会将supervisor重启，restart，而后，将supervisior的子类kill
        supervisor.tell(Props[Child],probe.ref)
        childActor = probe.expectMsgType[ActorRef](Duration.apply(1,TimeUnit.SECONDS))
        probe.watch(childActor)
        childActor!53
        childActor.tell("get",probe.ref)
        probe.expectMsg[Int](Duration.apply(1,TimeUnit.SECONDS),53)
        childActor!new Exception("crash")
        probe.expectMsgPF(){case t:Terminated=>{if (t.existenceConfirmed) {}}}
    }

    "Supervisor2" must "apply the chose strategy for its child" in {
        val probe = new TestProbe(system)
        val supervisor2 = system.actorOf(Props[Supervisor2])
        supervisor2.tell(Props[Child], probe.ref)
        var childActor = probe.expectMsgType[ActorRef](Duration.apply(1, TimeUnit.SECONDS))

        childActor!53
        childActor.tell("get",probe.ref)
        probe.expectMsg[Int](Duration.apply(1,TimeUnit.SECONDS),53)

        childActor!new Exception("crash")
        childActor.tell("get",probe.ref)
        probe.expectMsg[Int](Duration.apply(1,TimeUnit.SECONDS),0)
    }
}
