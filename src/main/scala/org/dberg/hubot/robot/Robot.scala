package org.dberg.hubot.robot

import org.dberg.hubot.utils.Logger
import org.dberg.hubot.adapter.BaseAdapter
import org.dberg.hubot.middleware.{MiddlewareError, MiddlewareSuccess}
import org.dberg.hubot.models.Message
import org.dberg.hubot.utils.Helpers._

trait RobotComponent {

  val robotService: RobotService
  val adapter: BaseAdapter
  def processMiddleware(message: Message): Either[MiddlewareError,MiddlewareSuccess]
  def processListeners(message: Message): Unit
  val helpCommands: Seq[Option[String]]

  class RobotService {


    val hubotName = getConfString("hubot.name","hubot")


    def receive(message: Message) = {
      Logger.log("Received message " + message)
      //Loop through middleware, halting if need be
      //then send to each listener
      processMiddleware(message) match {
        case Left(x) => Logger.log("Sorry, middleware error " + x.error)
        case Right(x) =>
          Logger.log("Middleware passed")
          processListeners(message)
      }
    }

    def send(message: Message) =
      adapter.send(message)

    def run() = {
      adapter.run()
    }
  }
}
