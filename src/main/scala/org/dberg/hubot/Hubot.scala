package org.dberg.hubot

import com.typesafe.config.{ConfigFactory, Config}
import com.typesafe.scalalogging.{StrictLogging, LazyLogging}
import org.dberg.hubot.adapter.{ShellAdapter, BaseAdapter}
import org.dberg.hubot.models._
import org.dberg.hubot.middleware._
import org.dberg.hubot.utils.Helpers._
import org.dberg.hubot.utils.Logger

object Hubot {

  def main(args: Array[String]) = {
    val config = ConfigFactory.load()
    val hubotName = getConfString("hubot.name","hubot")

    val listeners: Seq[Listener] = {
      getConfStringList("hubot.listeners").map { l =>
        Logger.log("Registering listener " + l,"info")
        Class.forName(l).newInstance().asInstanceOf[Listener]
      }
    }
    val helpCommands: Seq[String] = listeners.filter(l => l.helpString.isDefined).map(_.helpString.getOrElse(""))
    val allListeners = Seq(HelpListener(hubotName, helpCommands))

    def middleware: Seq[Middleware] = {
      getConfStringList("hubot.middleware").map { m =>
        Logger.log("Registering new middleware " + m,"info")
        Class.forName(m).newInstance().asInstanceOf[Middleware]
      }
    }

    val adapter: BaseAdapter = {
      val adapter = getConfString("hubot.adapter","org.dberg.hubot.adapter.ShellAdapter")
      Class.forName(adapter).newInstance().asInstanceOf[BaseAdapter]
    }

    val robot = new Robot(adapter, allListeners, middleware, hubotName)

    Logger.log("About to run robot : " + robot, "debug")
    robot.run()
  }

}
