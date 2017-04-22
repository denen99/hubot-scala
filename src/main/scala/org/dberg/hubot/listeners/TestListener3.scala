package org.dberg.hubot.listeners

import org.dberg.hubot.Hubot
import org.dberg.hubot.listeners.Listener.CallbackSuccess
import org.dberg.hubot.models._

class TestListener3(hubot: Hubot) extends Listener(hubot) {

  val callback: Callback = {
    case Direct() & SentBy(user) & Body(body @ "listen3") =>
      logger.debug("Running callback for listener TestListener3")
      robot.send(Message(user, body.reverse, MessageType.DirectMessage))

    case message & Room("room-1") & Group() =>
      logger.debug("Running callback for listener TestListener3")
      robot.send(Message(message.user, message.body.reverse, message.messageType))
  }

  val helpString = Some("listen3 -> reverses anything you send it")

}
