package org.dberg.hubot

import org.dberg.hubot.utils.Logger

object HubotRunner {

  def main(args: Array[String]): Unit = {
    
    val robot = new Hubot
    Logger.log("Found listeners " + robot.listeners)
    Logger.log("Found middleware " + robot.middleware)
    Logger.log("Using adapter " + robot.adapter)
    
    sys addShutdownHook {
      robot.brainService.shutdown
    }

    robot.robotService.run()
  }

}
