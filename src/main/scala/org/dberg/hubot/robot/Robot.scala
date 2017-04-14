package org.dberg.hubot.robot

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.adapter.BaseAdapter
import org.dberg.hubot.listeners.Listener
import org.dberg.hubot.middleware.{ Middleware, MiddlewareError, MiddlewareSuccess }
import org.dberg.hubot.models.{ Message }
import org.dberg.hubot.utils.Helpers._

trait RobotComponent {

  def robotService: RobotService
  def adapter: BaseAdapter
  def listeners: Seq[Listener]
  def middleware: List[Middleware]

  class RobotService extends StrictLogging {

    private def processMiddlewareRec(
      message: Message,
      m: List[Middleware],
      prevResult: Either[MiddlewareError, MiddlewareSuccess] = Right(MiddlewareSuccess())
    ): Either[MiddlewareError, MiddlewareSuccess] = m match {
      case Nil =>
        prevResult
      case h :: t if prevResult.isRight =>
        processMiddlewareRec(message, t, h.execute(message))
      case _ =>
        prevResult
    }

    def processMiddleware(message: Message) = processMiddlewareRec(message: Message, middleware)

    def processListeners(message: Message) = {
      listeners.foreach { l =>
        logger.debug("Processing message through listener " + l.toString)
        try {
          l.call(message)
        } catch {
          case e: Exception => logger.error("Error running listener " + e.printStackTrace)
        }
      }
    }

    val hubotName = getConfString("hubot.name", "hubot")

    def receive(message: Message) = {
      logger.debug("Robot received message " + message)
      //Loop through middleware, halting if need be
      //then send to each listener
      val cleanMessage = message.copy(body = message.body.trim)
      processMiddleware(cleanMessage) match {
        case Left(x) =>
          logger.error("Sorry, middleware error " + x.error)
        case Right(_) =>
          logger.debug("Middleware passed")
          processListeners(cleanMessage)
      }
    }

    def send(message: Message) =
      adapter.send(message)

    def run() = {
      adapter.run()
    }
  }
}
