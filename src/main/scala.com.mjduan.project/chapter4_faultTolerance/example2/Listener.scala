package chapter4_faultTolerance.example2

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration

import akka.actor.{Actor, ActorLogging, ReceiveTimeout}

import chapter4_faultTolerance.example2.Worker.Progress

/**
  * Hans 2017-08-05 17:24
  */
private[example2] class Listener extends Actor with ActorLogging {
   context.setReceiveTimeout(Duration.apply(15, TimeUnit.SECONDS))

   override def receive: Receive = {
      case Progress(percent) => processProgress(percent)
      case ReceiveTimeout => processReceiveTimeOut()
   }

   def processProgress(percent: Double) = {
      log.info("Current progress:{}%", percent)
      if (percent >= 100.0) {
         log.info("That's all, shutting down")
         context.system.terminate()
      }
   }

   def processReceiveTimeOut() = {
      log.error("Shutting down due to unavailable service")
      context.system.terminate()
   }
}
