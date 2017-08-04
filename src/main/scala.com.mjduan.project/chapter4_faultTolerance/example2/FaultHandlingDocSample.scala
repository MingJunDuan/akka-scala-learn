package chapter4_faultTolerance.example2

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive
import chapter4_faultTolerance.example2.Counter.UseStorage
import chapter4_faultTolerance.example2.CounterService.{CurrentCount, GetCurrentCount, Increment}
import chapter4_faultTolerance.example2.Storage.{Entry, Store}

/**
  * Hans 2017-08-04 14:45
  */
private[chapter4_faultTolerance] object FaultHandlingDocSample extends App {


}
object CounterService {
    sealed abstract class GetCurrentCount
    final case class Increment(n: Int)
    final case class CurrentCount(key: String, count: Long)
    class ServiceUnavailable(msg: String) extends RuntimeException(msg)
    case object GetCurrentCount extends GetCurrentCount
}
object Counter {
    final case class UseStorage(storage: Option[ActorRef])
}
class Counter(key: String, initialValue: Long) extends Actor {
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

    private def storeCount(): Unit = {
        //Let then DummyDB save key and count
        storage.foreach({ _ ! Store(Entry(key, count)) })
    }

    private def processIncrement(n: Int) = {
        count += n
        storeCount()
    }
}
object Storage {
    final case class Store(entry: Entry)
    final case class Get(key: String)
    final case class Entry(key: String, value: Long)
    class StorageException(msg: String) extends RuntimeException
}
class Storage extends Actor {
    import Storage._

    val db = DummyDB

    override def receive: Receive = {
        case Store(Entry(key, count)) => db.save(key, count)
        case Get(key) => sender() ! Entry(key, db.load(key).getOrElse(0L))
    }
}
object DummyDB {
    import Storage.StorageException

    val i: Int = 12
    private var db = Map[String, Long]()

    @throws(classOf[StorageException])
    def save(key: String, value: Long): Unit = synchronized {
        if (11 <= value && value <= 14) {
            throw new StorageException("store failure " + value)
        }
        db += (key -> value)
    }

    @throws(classOf[StorageException])
    def load(key: String): Option[Long] = synchronized {
        db.get(key)
    }
}