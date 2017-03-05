package org.dberg.hubot.brain

import org.mapdb._
import org.dberg.hubot.utils.Helpers._

object MapdbBackend extends BrainBackendBase {

  private val dbFile = getConfString("hubot.brainFile","/tmp/brain.db")
  private val db = DBMaker.fileDB(dbFile).make()
  private val dbHash = db.hashMap("hubot",Serializer.STRING,Serializer.STRING).createOrOpen()

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