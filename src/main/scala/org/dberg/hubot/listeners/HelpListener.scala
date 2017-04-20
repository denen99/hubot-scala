package org.dberg.hubot.listeners

import org.dberg.hubot.Hubot
import org.dberg.hubot.listeners.Listener.CallbackSuccess
import org.dberg.hubot.models._

class HelpListener(hubot: Hubot) extends Listener(hubot) {

  lazy val helpCommands = hubot.listeners.flatMap(_.helpString)

  val callback: Callback = {
    case message @ Body(r"^help\s*(.*)$command") =>
      logger.debug("Running help listener")

      lazy val commands = if (command.nonEmpty)
        helpCommands.filter(h => h.contains(command))
      else
        helpCommands

      robot.send(Message(message.user, "Help commands \n" + commands.mkString("\n"), message.messageType))
  }

  val helpString = Some("help -> list all available commands ")
}
