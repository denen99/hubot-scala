package org.dberg.hubot.listeners

import org.dberg.hubot.Hubot
import org.dberg.hubot.listeners.Listener.CallbackSuccess
import org.dberg.hubot.models._

class TestListener2(hubot: Hubot) extends Listener(hubot) {

  val callback: Callback = {
    case message @ Body("listen2") =>
      logger.debug("Running callback for listener TestListener2")
      robot.send(Message(message.user, message.body.reverse, message.messageType))
  }

  val helpString = Some("listen2 -> reverses anything you send it ")

}
