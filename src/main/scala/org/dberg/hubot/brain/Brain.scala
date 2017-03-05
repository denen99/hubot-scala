package org.dberg.hubot.brain

import org.dberg.hubot.utils.Helpers._

trait BrainComponent { 

  val brainService: BrainService

  class BrainService {

    private val backend = {
      getConfString("hubot.brain","mapdb") match {
        case "mapdb" => MapdbBackend
        case _ => MapdbBackend
      }
    }
    
    def set(key: String, value: String) = backend.setKey(key,value)

    def get(key: String) = backend.getKey(key)

    def shutdown = backend.shutdown() 
    
  }

}


