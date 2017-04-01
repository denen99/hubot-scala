package org.dberg.hubot.brain

import org.dberg.hubot.utils.Helpers._
import scodec.{ Codec ⇒ SCodec, _ }
import scodec.codecs.implicits._

trait BrainComponent {

  def brainService: BrainService

  class BrainService {

    private val backend = {
      getConfString("hubot.brain", "mapdb") match {
        case "mapdb" => MapdbBackend
        case _ => MapdbBackend
      }
    }

    def set[A: SCodec](key: String, value: A) =
      backend.setKey[A](key, value)

    def get[A: SCodec](key: String) = backend.getKey[A](key)

    def shutdown = backend.shutdown()

  }

}

