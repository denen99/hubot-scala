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
    val robot = Robot.robotService
    Logger.log("Found listeners " + Robot.listeners)
    Logger.log("Found middleware " + Robot.middleware)
    Logger.log("Using adapter " + Robot.adapter)
    robot.run()

  }

}
