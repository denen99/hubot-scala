package org.dberg.hubot.listeners

import org.dberg.hubot.{ Hubot, HubotBase }
import org.dberg.hubot.models.{ Message, User }
import org.dberg.hubot.models.MessageType.DirectMessage

class SpecListener(hubot: HubotBase) extends Listener(hubot, "spectest\\s*(.*)") {

  val helpString = Some("this is a spec helper")

  override def runCallback(message: Message, groups: List[String]): Unit = {
    logger.debug("running spec listener callback")
    hubot.brainService.set[String]("lastmessage", message.body)
    if (groups.head.isEmpty) {
      hubot.adapter.send(Message(User("specuser"), "received", DirectMessage))
    } else {
      hubot.adapter.send(Message(User("specuser"), "received " + groups.head, DirectMessage))
    }

  }
}
