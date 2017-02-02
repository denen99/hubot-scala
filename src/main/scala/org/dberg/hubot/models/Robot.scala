package org.dberg.hubot.models

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.adapter.BaseAdapter
import org.dberg.hubot.utils.Logger
import org.dberg.hubot.middleware.{MiddlewareSuccess, MiddlewareError, Middleware}
import org.slf4j.impl.StaticLoggerBinder

class Robot(adapter: BaseAdapter,
            val listeners: Seq[Listener],
            val middleware: Seq[Middleware],
            val name: String
                 )  {

  def processMiddlewareRec(message: Message,
                           m: Seq[Middleware],
                           prevResult: Either[MiddlewareError,MiddlewareSuccess] = Right(MiddlewareSuccess())): Either[MiddlewareError,MiddlewareSuccess] = m match {
    case Nil => prevResult
    case h :: t if prevResult.isRight => processMiddlewareRec(message,m.tail,h.execute(message))
    case _ =>  prevResult
  }

  def processMiddleware(message: Message) = processMiddlewareRec(message: Message, middleware)

  def processListeners( message: Message) = {
    listeners.foreach { l => l.call(message) }
  }

  def receive(message: Message) = {
    println(message)
    //Loop through middleware, halting if need be
    //then send to each listener
    processMiddleware(message) match {
      case Left(x) => println("Sorry error " + x.error)
      case Right(x) =>
        println(message)
        processListeners(message)
    }
  }

  def send(message: Message) =
    adapter.send(message)

  def run() = {
    adapter.run()
  }
}

