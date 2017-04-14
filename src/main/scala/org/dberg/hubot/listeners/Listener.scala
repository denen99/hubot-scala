package org.dberg.hubot.listeners

import java.util.regex.{ Matcher, Pattern }

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.listeners.Listener._
import org.dberg.hubot.{ Hubot, HubotBase }
import org.dberg.hubot.listeners.ListenerType.ListenerValue
import org.dberg.hubot.models.Message
import org.dberg.hubot.utils.Helpers._
import scodec.codecs.ImplicitCodecs
import scala.util.control.NonFatal

object Listener {

  sealed trait CallbackResult
  case object CallbackSuccess extends CallbackResult //Callback worked
  case class CallbackFailure(reason: Throwable) extends CallbackResult //Callback threw a failure
  case object CallbackSkipped extends CallbackResult //Message not intended for robot
  case object CallbackNotMatched extends CallbackResult //Message is for robot, but no listener matches
  case object CallbackMiddlewareFailure extends CallbackResult //Callback not invoked due to middleware failure
}

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

  def call(message: Message): CallbackResult = {
    if (shouldRespond(message)) {
      val matcher = pattern.matcher(message.body.removeBotString(robot.hubotName))

      if (matcher.find()) {
        val groups = buildGroups(matcher, matcher.groupCount())
        try {
          runCallback(message.copy(
            body = message.body.removeBotString(robot.hubotName)
          ), groups)
        } catch {
          case NonFatal(e) => CallbackFailure(e)
        }
      } else {
        logger.debug("no match for listener " + this.getClass.getName)
        CallbackNotMatched
      }

    } else {
      logger.debug("Sorry, listeners says we should not respond")
      CallbackSkipped
    }
  }

  private def shouldRespond(message: Message): Boolean = {
    listenerType == ListenerType.Hear ||
      (listenerType == ListenerType.Respond && message.body.addressedToHubot(message, robot.hubotName))
  }

  def runCallback(message: Message, groups: List[String]): CallbackResult

  def helpString: Option[String]
}
