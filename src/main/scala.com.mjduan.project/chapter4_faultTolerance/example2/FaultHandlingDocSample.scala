package chapter4_faultTolerance.example2

import akka.actor.{ActorSystem, Props}

import chapter4_faultTolerance.example2.Worker.Start
import com.typesafe.config.ConfigFactory

/**
  * Hans 2017-08-04 14:45
  */
private[chapter4_faultTolerance] object FaultHandlingDocSample extends App {

   val config = ConfigFactory.parseString(
      """
        akka.loglevel="DEBUG"
        akka.actor.debug{
            receive=on
            lifecycle=on
        }
      """.stripMargin)

   val system = ActorSystem("FaulttoleranceSample",config)
   val worker = system.actorOf(Props[Worker],name = "worker")
   val listener = system.actorOf(Props[Listener],name = "listener")
   worker.tell(Start,sender = listener)

}
