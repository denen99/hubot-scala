package org.dberg.hubot

import com.typesafe.config.{ConfigFactory, Config}
import org.dberg.hubot.utils.Helpers._
import org.dberg.hubot.utils.Logger

object HubotRunner {

  def main(args: Array[String]) = {
    val config = ConfigFactory.load()
    val hubotName = getConfString("hubot.name","hubot")
    val robot = new Hubot
    Logger.log("Found listeners " + robot.listeners)
    Logger.log("Found middleware " + robot.middleware)
    Logger.log("Using adapter " + robot.adapter)
    robot.robotService.run()

  }

}
