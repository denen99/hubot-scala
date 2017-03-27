package org.dberg.hubot.listeners

import java.util.regex.{ Matcher, Pattern }

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.Hubot
import org.dberg.hubot.listeners.ListenerType.ListenerValue
import org.dberg.hubot.models.Message
import org.dberg.hubot.utils.Helpers._

abstract class Listener(
    val hubot: Hubot,
    matcher: String,
    listenerType: ListenerValue = ListenerType.Respond
) extends StrictLogging {
  val pattern = Pattern.compile(matcher)
  val robot = hubot.robotService
  val brain = hubot.brainService
  val event = hubot.eventService

  def buildGroups(matcher: Matcher, count: Int, results: Seq[String] = Seq()): Seq[String] = count match {
    case 0 => results
    case x => buildGroups(matcher, count - 1, results :+ matcher.group(count))
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

  def shouldRespond(message: Message): Boolean = {
    println(s"message $message $listenerType ${message.body} $robot.hubotName")
    listenerType == ListenerType.Hear || (listenerType == ListenerType.Respond && message.body.addressedToHubot(message, robot.hubotName))
  }

  def runCallback(message: Message, groups: Seq[String]): Unit

  def helpString: Option[String]
}
