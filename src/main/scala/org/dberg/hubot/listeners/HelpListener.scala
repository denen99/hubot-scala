package org.dberg.hubot.listeners

import org.dberg.hubot.Hubot
import org.dberg.hubot.models.Message

class HelpListener(hubot: Hubot) extends Listener(hubot, "^help\\s*(.*)") {

  lazy val helpCommands = hubot.listeners.flatMap(l => l.helpString)

  def runCallback(message: Message, groups: Seq[String]) = {
    logger.debug("Running help listener")

    lazy val commands = if (groups.nonEmpty)
      helpCommands.filter(h => h.contains(groups.head))
    else
      helpCommands

    robot.send(Message(message.user, "Help commands \n" + commands.mkString("\n"), message.messageType))
  }

  val helpString = Some("help -> list all available commands ")
}
