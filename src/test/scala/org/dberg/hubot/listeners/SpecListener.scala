package org.dberg.hubot.listeners

import org.dberg.hubot.{ Hubot, HubotBase }
import org.dberg.hubot.models.{ Message, User }
import org.dberg.hubot.SpecHelpers._

class SpecListener(hubot: HubotBase) extends Listener(hubot, "spectest\\s*(.*)") {

  val helpString = Some("this is a spec helper")

  override def runCallback(message: Message, groups: List[String]): Unit = {
    logger.debug("running spec listener callback")
    val resp = generateListenerResponse(message, groups.head)
    hubot.brainService.set[String]("lastmessage", resp.body)
    hubot.adapter.send(resp)
  }
}
