package chapter4_faultTolerance.example2

import akka.actor.Actor

/**
  * Hans 2017-08-05 17:32
  */
private[example2] class Storage extends Actor {

   import Storage._

   val db = DummyDB

   override def receive: Receive = {
      case Store(Entry(key, count)) => db.save(key, count)
      case Get(key) => sender() ! Entry(key, db.load(key).getOrElse(0L))
   }
}

private[example2] object Storage {
   final case class Store(entry: Entry)
   final case class Get(key: String)
   final case class Entry(key: String, value: Long)
   class StorageException(msg: String) extends RuntimeException
}


private[example2] object DummyDB {

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
