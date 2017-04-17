package org.dberg.hubot.brain

import com.typesafe.scalalogging.StrictLogging
import org.mapdb._
import org.dberg.hubot.utils.Helpers._
import scodec.{ Codec => SCodec, _ }
import scodec.codecs.implicits._

import scala.util.Try

object MapdbBackend extends BrainBackendBase with StrictLogging {

  logger.info("About to setup MapDB")
  private val dbFile = getConfString("hubot.brainFile", "/tmp/brain.db")
  private val db = DBMaker.fileDB(dbFile).closeOnJvmShutdown().make()
  private val dbHash = db.hashMap("hubot", Serializer.STRING, Serializer.BYTE_ARRAY).createOrOpen()
  logger.info("Done with MapDB Setup")

  def deleteAll() =
    dbHash.getKeys.toArray.toList.foreach(key => dbHash.remove(key))

  def setKey[A: SCodec](key: String, value: A) =
    dbHash.put(key, encode(value).getOrElse(Array()))

  def getKey[A: SCodec](key: String): Try[A] = {
    val result = dbHash.get(key)
    decode[A](result)
  }

  def shutdown() = {
    logger.info("Shutting down MapDB")
    dbHash.close()
    db.close()
  }

}