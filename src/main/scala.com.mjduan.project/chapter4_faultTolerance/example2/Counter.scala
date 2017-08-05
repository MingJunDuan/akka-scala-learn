package chapter4_faultTolerance.example2

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive

import chapter4_faultTolerance.example2.Counter.UseStorage
import chapter4_faultTolerance.example2.CounterService.{CurrentCount, GetCurrentCount, Increment}
import chapter4_faultTolerance.example2.Storage.{Entry, Store}

/**
  * Hans 2017-08-05 17:30
  */
private[example2] class Counter(key: String, initialValue: Long) extends Actor {
   private var count = initialValue
   private var storage: Option[ActorRef] = None

   override def receive: Receive = LoggingReceive {
      case UseStorage(s) => processUseStorage(s)
      case Increment(n) => processIncrement(n)
      case GetCurrentCount => sender() ! CurrentCount(key, count)
   }

   private def processUseStorage(s: Option[ActorRef]) = {
      storage = s
      storeCount()
   }

   private def processIncrement(n: Int) = {
      count += n
      storeCount()
   }

   private def storeCount(): Unit = {
      //Let then DummyDB save key and count
      storage.foreach({ _ ! Store(Entry(key, count)) })
   }
}

private[example2] object Counter {
   final case class UseStorage(storage: Option[ActorRef])
}