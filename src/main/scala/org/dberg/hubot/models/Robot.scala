package org.dberg.hubot.models

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.adapter.BaseAdapter
import org.dberg.hubot.utils.Logger
import org.dberg.hubot.middleware.{Middleware, MiddlewareError, MiddlewareSuccess}
import org.dberg.hubot.utils.Helpers.{getConfString, getConfStringList}
import org.slf4j.impl.StaticLoggerBinder

object Robot {


  val hubotName = getConfString("hubot.name","hubot")

  val adapter: BaseAdapter = {
    val adapter = getConfString("hubot.adapter","org.dberg.hubot.adapter.ShellAdapter")
    Class.forName(adapter).newInstance().asInstanceOf[BaseAdapter]
  }

  val middleware = {
    getConfStringList("hubot.middleware").map { m =>
      Logger.log("Registering new middleware " + m,"info")
      Class.forName(m).newInstance().asInstanceOf[Middleware]
    }
  }

  val listeners: Seq[Listener] = {
    getConfStringList("hubot.listeners").map { l =>
      Logger.log("Registering listener " + l,"info")
      Class.forName(l).newInstance().asInstanceOf[Listener]
    }
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

