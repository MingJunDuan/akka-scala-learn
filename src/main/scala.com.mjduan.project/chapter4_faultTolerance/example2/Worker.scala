package chapter4_faultTolerance.example2

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props}
import akka.event.LoggingReceive
import akka.util.Timeout

import chapter4_faultTolerance.example2.CounterService.{CurrentCount, GetCurrentCount, Increment}
import chapter4_faultTolerance.example2.Worker.{Do, Progress, Start}

/**
  * Hans 2017-08-05 16:39
  */
private[example2] class Worker extends Actor with ActorLogging {

   implicit val askTimeOut = Timeout.apply(5, TimeUnit.SECONDS)

   override val supervisorStrategy = OneForOneStrategy() {
      case _: CounterService.ServiceUnavailable => Stop
   }
   val counterService = context.actorOf(Props[CounterService], name = "counter")
   val totalCount = 51
   var progressListener: Option[ActorRef] = None

   override def receive: LoggingReceive = {
      case Start if progressListener.isEmpty => processStartWithProcessListenerEmpty()
      case Do => processDo()
   }

   def processStartWithProcessListenerEmpty() = {
      progressListener = Some(sender())
      context.system.scheduler.schedule(Duration.Zero, Duration.apply(1, TimeUnit.SECONDS), self, Do)
   }

   def processDo() = {
      counterService ! Increment(1)
      counterService ! Increment(1)
      counterService ! Increment(1)

      counterService ? GetCurrentCount map {
         case CurrentCount(_, count) => Progress(100.0 * count / totalCount)
      } pipeTo progressListener.get
   }
}

private[example2] object Worker {
   final case class Progress(percent: Double)
   case object Start
   case object Do
}
