package org.dberg.hubot.brain

import org.mapdb._
import org.dberg.hubot.utils.Helpers._

object MapdbBackend extends BrainBackendBase {

  val dbFile = getConfString("hubot.brainFile","/tmp/brain.db")
  val db = DBMaker.fileDB("testfile").make()
  val dbHash = db.hashMap("hubot",Serializer.STRING,Serializer.STRING).createOrOpen()

  def setKey(key: String, value: String) = {
    dbHash.put(key,value)
  }

  def getKey(key: String) = {
    dbHash.get(key)
  }

  def shutdown() = {
    dbHash.close()
    db.close()
  }
}