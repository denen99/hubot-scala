package org.dberg.hubot

import com.typesafe.scalalogging.StrictLogging

object HubotRunner extends StrictLogging {

  def main(args: Array[String]): Unit = {
    start
  }

  def start = {
    val robot = new Hubot

    logger.debug("Found listeners " + robot.listeners)
    logger.debug("Found middleware " + robot.middleware)
    logger.debug("Using adapter " + robot.adapter)

    sys addShutdownHook {
      robot.brainService.shutdown
    }

    robot.robotService.run()
  }

}
