package org.dberg.hubot.listeners

import org.dberg.hubot.{ Hubot, HubotBase }
import org.dberg.hubot.models.{ Message, User }
import org.dberg.hubot.SpecHelpers._
import org.dberg.hubot.listeners.Listener.{ CallbackFailure, CallbackSuccess }

import scala.util.control.NonFatal

class SpecListener(hubot: HubotBase) extends Listener(hubot, "spectest\\s*(.*)") {

  val helpString = Some("this is a spec helper")

  override def runCallback(message: Message, groups: List[String]) = {
    logger.debug("running spec listener callback")
    val resp = generateListenerResponse(message, groups.head)

    //Bogus data to simulate a failure
    if (message.body.contains("failure")) {
      CallbackFailure(exception)
    } else {
      hubot.brainService.set[String]("lastmessage", resp.body)
      hubot.adapter.send(resp)
      CallbackSuccess
    }

  }
}
