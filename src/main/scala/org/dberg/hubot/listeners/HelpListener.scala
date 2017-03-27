package org.dberg.hubot.listeners

import org.dberg.hubot.Hubot
import org.dberg.hubot.models.Message

/**
 * Created by adenenberg on 3/27/17.
 */
class HelpListener(hubot: Hubot) extends Listener(hubot, "^help") {

  lazy val helpCommands = hubot.listeners.flatMap(l => l.helpString)

  def runCallback(message: Message, groups: Seq[String]) = {
    logger.debug("Running help listener")
    hubot.robotService.send(Message(message.user, "Help commands \n" + helpCommands.mkString("\n"), message.messageType))
  }

  val helpString = Some("help -> list all available commands ")
}
