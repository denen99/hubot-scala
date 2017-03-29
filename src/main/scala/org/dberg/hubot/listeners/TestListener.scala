package org.dberg.hubot.listeners

import org.dberg.hubot.Hubot
import org.dberg.hubot.event.Event
import org.dberg.hubot.models.Message
import scodec.codecs.implicits._

class TestListener(hubot: Hubot) extends Listener(hubot, "listen1\\s+(.*)", ListenerType.Hear) {

  def runCallback(message: Message, groups: Seq[String]) = {
    val lastMessage = brain.get[String]("lastmessage")
    val resp = "scalabot heard you mention " + groups.head + " !, the last thing you said was " + lastMessage
    brain.set[String]("lastmessage", message.body)
    logger.debug("Running callback for listener TestListener, sending response " + resp)
    event.emit(Event("testid", Map("test" -> "value")))
    hubot.robotService.send(Message(message.user, resp, message.messageType))
  }

  val helpString = Some("listen1 -> Responds to anything and repeats it ")
}
