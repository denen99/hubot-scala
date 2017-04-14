package org.dberg.hubot.listeners

import java.util.regex.{ Matcher, Pattern }

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.{ Hubot, HubotBase }
import org.dberg.hubot.listeners.ListenerType.ListenerValue
import org.dberg.hubot.models.Message
import org.dberg.hubot.utils.Helpers._
import scodec.codecs.ImplicitCodecs

abstract class Listener(
    val hubot: HubotBase,
    matcher: String,
    listenerType: ListenerValue = ListenerType.Respond
) extends StrictLogging with ImplicitCodecs {
  val pattern = Pattern.compile(matcher)
  val robot = hubot.robotService
  val brain = hubot.brainService
  val event = hubot.eventService

  private def buildGroups(matcher: Matcher, count: Int, results: List[String] = List()): List[String] = count match {
    case 0 => results.reverse
    case _ => buildGroups(matcher, count - 1, results :+ matcher.group(count))
  }

  def call(message: Message): Unit = {
    if (shouldRespond(message)) {
      val matcher = pattern.matcher(message.body.removeBotString(robot.hubotName))
      matcher.find() match {
        case false => logger.debug("no match for listener " + this.getClass.getName)
        case true =>
          val groups = buildGroups(matcher, matcher.groupCount())
          runCallback(message.copy(
            body = message.body.removeBotString(robot.hubotName)
          ), groups)
      }
    } else {
      logger.debug("Sorry, listeners says we should not respond")
    }
  }

  private def shouldRespond(message: Message): Boolean = {
    listenerType == ListenerType.Hear ||
      (listenerType == ListenerType.Respond && message.body.addressedToHubot(message, robot.hubotName))
  }

  def runCallback(message: Message, groups: List[String]): Unit

  def helpString: Option[String]
}
