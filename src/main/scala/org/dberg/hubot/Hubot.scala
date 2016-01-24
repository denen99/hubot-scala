package org.dberg.hubot

import com.typesafe.config.{ConfigFactory, Config}
import com.typesafe.scalalogging.{StrictLogging, LazyLogging}
import org.dberg.hubot.adapter.{TestAdapter, BaseAdapter}
import org.dberg.hubot.models._
import org.dberg.hubot.middleware._
import org.dberg.hubot.utils.Helpers._
import org.dberg.hubot.utils.Logger


trait HubotBase {

  val config = ConfigFactory.load()

  def robot: Robot
  def adapter: BaseAdapter
  def listeners: Seq[Listener]
  def middleware: Seq[Middleware]

  def main(args: Array[String]) = {
    Logger.log("About to run robot : " + robot, "debug")
    robot.run()
  }

  val defaultListeners = Seq(HelpListener())

  def getListeners: Seq[Listener] = {
    getConfStringList("hubot.listeners").map { l =>
      Logger.log("Registering listener " + l,"info")
      Class.forName(l).newInstance().asInstanceOf[Listener]
    } ++ defaultListeners
  }

  def getMiddleware: Seq[Middleware] = {
    getConfStringList("hubot.middleware").map { m =>
      Logger.log("Registering new middleware " + m,"info")
      Class.forName(m).newInstance().asInstanceOf[Middleware]
    }
  }

  def getAdapter : BaseAdapter = {
    val adapter = getConfString("hubot.adapter","org.dberg.hubot.adapter.TestAdapter")
    Class.forName(adapter).newInstance().asInstanceOf[BaseAdapter]
  }

}


object Hubot extends HubotBase   {

  val listeners = getListeners
  val middleware = getMiddleware
  val adapter = getAdapter
  val hubotName = getConfString("hubot.name","hubot")

  val robot = new Robot(adapter, listeners, middleware,hubotName)

  val helpCommands: Seq[String] = listeners.filter(l => l.helpString.isDefined).map(_.helpString.getOrElse(""))

}
