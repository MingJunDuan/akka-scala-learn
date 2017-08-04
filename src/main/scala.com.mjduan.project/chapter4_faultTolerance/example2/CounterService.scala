package chapter4_faultTolerance.example2

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, OneForOneStrategy, SupervisorStrategy}
import akka.actor.Actor.Receive
import akka.actor.SupervisorStrategy.Restart

import scala.concurrent.duration.Duration

/**
  * Hans 2017-08-04 15:39
  */
class CounterService extends Actor{

    //Every exception, then Restart it
    override val supervisorStrategy = OneForOneStrategy(
        maxNrOfRetries = 3,withinTimeRange = Duration.apply(1,TimeUnit.SECONDS) ){
        case _:Storage.StorageException=>Restart
    }

    val key = self.path.name
    var storage:Option[ActorRef]=None
    var counter:Option[ActorRef]=None
    var backlog=IndexedSeq.empty[(ActorRef,Any)]
    val maxBackLog=1000

    def initStorage() = {
        storage =
    }

    override def preStart(): Unit = initStorage()

    override def receive: Receive = {

    }
}
