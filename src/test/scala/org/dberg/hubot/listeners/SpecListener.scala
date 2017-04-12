package org.dberg.hubot.listeners

import org.dberg.hubot.{ Hubot, HubotBase }
import org.dberg.hubot.models.Message

class SpecListener(hubot: HubotBase) extends Listener(hubot, "spectest\\s*(.*)") {

  val helpString = Some("this is a spec helper")

  override def runCallback(message: Message, groups: Seq[String]): Unit = {
    brain.set[String]("lastmessage", message.body)
    logger.info("Spec running listener callback")
  }
}
