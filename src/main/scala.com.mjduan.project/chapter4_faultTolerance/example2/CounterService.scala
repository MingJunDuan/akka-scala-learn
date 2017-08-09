package chapter4_faultTolerance.example2

import java.util.concurrent.TimeUnit
import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props, Terminated}
import akka.event.LoggingReceive
import chapter4_faultTolerance.example2.Counter.UseStorage
import chapter4_faultTolerance.example2.CounterService.{GetCurrentCount, Increment, Reconnect, ServiceUnavailable}
import chapter4_faultTolerance.example2.Storage.{Entry, Get}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Hans 2017-08-04 15:39
  */
class CounterService extends Actor {
    //Every exception, then Restart it
    override val supervisorStrategy = OneForOneStrategy(
        maxNrOfRetries = 3, withinTimeRange = Duration.apply(1, TimeUnit.SECONDS)) {
        case _: Storage.StorageException => Restart
    }
    val key = self.path.name
    val maxBackLog = 1000
    var storage: Option[ActorRef] = None
    var counter: Option[ActorRef] = None
    var backlog = IndexedSeq.empty[(ActorRef, Any)]

    override def preStart(): Unit = initStorage()

    override def receive = LoggingReceive {
        case Entry(k, v) if k == key && counter == None => processEntry(k, v)
        case msg: Increment => forwardOrPlaceInBacklog(msg)
        case msg: GetCurrentCount => forwardOrPlaceInBacklog(msg)
        case Terminated(actorRef) if Some(actorRef) == storage => processTerminated()
        case Reconnect => initStorage()
    }

    private def initStorage() = {
        storage = Some(context.watch(context.actorOf(Props[Storage], name = "storage")))
        counter.foreach(c => c ! UseStorage(storage))
        storage.get ! Get(key)
    }

    private def processEntry(key: String, value: Long): Unit = {
        val c = context.actorOf(Props(classOf[Counter], key, value))
        counter = Some(c)
        c ! UseStorage(storage)
        for ((replyTo, msg) <- backlog)
            c.tell(msg, sender = replyTo)
        backlog = IndexedSeq.empty
    }

    private def forwardOrPlaceInBacklog(msg: Any) = {
        counter match {
            case Some(c) => c forward (msg)
            case None => {
                if (backlog.size >= maxBackLog) {
                    throw new ServiceUnavailable("CounterService not available, lack of initial value")
                }
                backlog :+= (sender() -> msg)
            }
        }
    }

    def processTerminated() = {
        storage = None
        counter foreach { _ ! Storage }
        context.system.scheduler.scheduleOnce(Duration.apply(10, TimeUnit.SECONDS), self, Reconnect)
    }

}
object CounterService {
    sealed abstract class GetCurrentCount
    final case class Increment(n: Int)
    final case class CurrentCount(key: String, count: Long)
    class ServiceUnavailable(msg: String) extends RuntimeException(msg)
    case object GetCurrentCount extends GetCurrentCount
    private case object Reconnect
}
