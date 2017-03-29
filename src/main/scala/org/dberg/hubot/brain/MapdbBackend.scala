package org.dberg.hubot.brain

import org.mapdb._
import org.dberg.hubot.utils.Helpers._
import scodec.{ Codec ⇒ SCodec, _ }
import scodec.codecs.implicits._

object MapdbBackend extends BrainBackendBase {

  private val dbFile = getConfString("hubot.brainFile", "/tmp/brain.db")
  private val db = DBMaker.fileDB(dbFile).make()
  private val dbHash = db.hashMap("hubot", Serializer.STRING, Serializer.BYTE_ARRAY).createOrOpen()

  def setKey[A: SCodec](key: String, value: A) = {
    dbHash.put(key, encode(value))
  }

  def getKey[A: SCodec](key: String) = {
    val result = dbHash.get(key)
    decode[A](result)
  }

  def shutdown() = {
    dbHash.close()
    db.close()
  }

}