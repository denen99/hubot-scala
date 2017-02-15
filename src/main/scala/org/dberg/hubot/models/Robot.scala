package org.dberg.hubot.models

import org.dberg.hubot.adapter.{BaseAdapter, HipchatAdapter, ShellAdapter}
import org.dberg.hubot.utils.Logger
import org.dberg.hubot.middleware.{Middleware, MiddlewareError, MiddlewareSuccess, TestMiddleware}
import org.dberg.hubot.utils.Helpers.{getConfString, getConfStringList}

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

object Robot extends RobotComponent {
  val robotService = new RobotService

  val listeners: Seq[Listener] = {
    getConfStringList("hubot.listeners").map({
      case "test" =>
        Logger.log("Registering listener: test","debug")
        new TestListener(robotService)
      case "test2" =>
        Logger.log("Registering listener: test2","debug")
        new TestListener2(robotService)
      case "help" =>
        Logger.log("Registering listener: help","debug")
        new HelpListener(robotService)
      case x =>
        Logger.log("Sorry, unknown listener " + x,"debug")
        throw new Exception("Invalid listener in configuration, " + x.toString)
    })
  }

  val helpCommands = listeners.map(l => l.helpString).filter(l => l.isDefined)

  val adapter: BaseAdapter = {
    val a = getConfString("hubot.adapter","shell")
    a match {
      case "shell" => new ShellAdapter(robotService)
      case "hipchat" => new HipchatAdapter(robotService)
    }
  }

  val middleware = {
    getConfStringList("hubot.middleware").map({
      case "test" => TestMiddleware
      case x => Logger.log("Sorry, uknown middleware in config " + x,"debug")
    }).asInstanceOf[Seq[Middleware]]
  }

  private def processMiddlewareRec(message: Message,
                                   m: Seq[Middleware],
                                   prevResult: Either[MiddlewareError,MiddlewareSuccess] = Right(MiddlewareSuccess())): Either[MiddlewareError,MiddlewareSuccess] = m match {
    case Nil => prevResult
    case h :: t if prevResult.isRight => processMiddlewareRec(message,m.tail,h.execute(message))
    case _ =>  prevResult
  }

  def processMiddleware(message: Message) = processMiddlewareRec(message: Message, middleware)

  def processListeners( message: Message) = {
    listeners.foreach { l => Logger.log("Processing message through listener " + l.toString);   l.call(message) }
  }

}

