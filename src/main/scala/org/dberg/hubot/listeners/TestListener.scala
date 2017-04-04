package org.dberg.hubot.listeners

import org.dberg.hubot.Hubot
import org.dberg.hubot.event.Event
import org.dberg.hubot.models.Message

import scala.util.{Failure, Success}

class TestListener(hubot: Hubot) extends Listener(hubot, "listen1\\s+(.*)", ListenerType.Hear) {

  def runCallback(message: Message, groups: Seq[String]) = {
    val lastMessage = brain.get[String]("lastmessage") match {  //contrived example to show a way to handle this
      case Success(x) => x
      case Failure(y) => ""
    }
    //Could also do above as brain.get[String]("lastmessage").getOrElse("")

    val resp = "scalabot heard you mention " + groups.head + " !, the last thing you said was " + lastMessage
    brain.set[String]("lastmessage", message.body)
    logger.debug("Running callback for listener TestListener, sending response " + resp)
    event.emit(Event("testid", Map("test" -> "value")))
    robot.send(Message(message.user, resp, message.messageType))
  }

  val helpString = Some("listen1 -> Responds to anything and repeats it ")
}
