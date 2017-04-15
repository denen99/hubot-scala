package org.dberg.hubot.listeners

import org.dberg.hubot.Hubot
import org.dberg.hubot.listeners.Listener.CallbackSuccess
import org.dberg.hubot.models.Message

class TestListener2(hubot: Hubot) extends Listener(hubot, "listen2") {

  def runCallback(message: Message, groups: List[String]) = {
    logger.debug("Running callback for listener TestListener2")
    robot.send(Message(message.user, message.body.reverse, message.messageType))
    CallbackSuccess
  }

  val helpString = Some("listen2 -> reverses anything you send it ")

}
