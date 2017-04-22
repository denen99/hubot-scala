package org.dberg.hubot.listeners

import util.matching.Regex
import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.listeners.Listener._
import org.dberg.hubot.{ Hubot, HubotBase }
import org.dberg.hubot.listeners.ListenerType.ListenerValue
import org.dberg.hubot.models.Message
import org.dberg.hubot.utils.Helpers._
import scodec.codecs.ImplicitCodecs

import scala.annotation.tailrec
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
    listenerType: ListenerValue = ListenerType.Respond
) extends StrictLogging with ImplicitCodecs {

  type Callback = PartialFunction[Message, Unit]

  implicit class RegexContext(sc: StringContext) {
    def r = new Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  def callback: Callback

  val robot = hubot.robotService
  val brain = hubot.brainService
  val event = hubot.eventService

  def call(message: Message): CallbackResult = {
    if (shouldRespond(message)) {
      val updated = message.copy(
        body = message.body.removeBotString(robot.hubotName)
      )
      if (callback.isDefinedAt(updated)) {
        try {
          callback(updated)
          CallbackSuccess
        } catch {
          case NonFatal(e) => CallbackFailure(e)
        }
      } else {
        logger.debug("no match for listener " + this.getClass.getName)
        CallbackNotMatched
      }

    } else {
      CallbackSkipped
    }
  }

  private def shouldRespond(message: Message): Boolean = {
    listenerType == ListenerType.Hear ||
      (listenerType == ListenerType.Respond && message.body.addressedToHubot(message, robot.hubotName))
  }

  def helpString: Option[String]
}
