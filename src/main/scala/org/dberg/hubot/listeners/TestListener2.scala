package org.dberg.hubot.listeners

import org.dberg.hubot.Hubot
import org.dberg.hubot.models.Message

class TestListener2(hubot: Hubot) extends Listener(hubot, "listen2") {

  def runCallback(message: Message, groups: Seq[String]) = {
    logger.debug("Running callback for listener TestListener2")
    hubot.robotService.send(Message(message.user, message.body.reverse, message.messageType))
  }

  val helpString = Some("listen2 -> reverses anything you send it ")

}
