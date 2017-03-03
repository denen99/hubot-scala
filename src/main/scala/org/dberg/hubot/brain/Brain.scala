package org.dberg.hubot.brain

import org.mapdb._

trait BrainComponent {

  val brainService: BrainService

  class BrainService {

    val db = DBMaker.fileDB("testfile").make()
    val dbHash = db.hashMap("hubot",Serializer.STRING,Serializer.STRING).createOrOpen()

     def set(key: String, value: String) = {
        dbHash.put(key,value)
     }

     def get(key: String, value: String) = {
        dbHash.get(key)
     }
  }
}


