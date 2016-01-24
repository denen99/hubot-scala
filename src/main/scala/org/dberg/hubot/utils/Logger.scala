package org.dberg.hubot.utils

import com.typesafe.scalalogging.{StrictLogging}

object Logger extends StrictLogging {

  def log(msg: String, level: String = "debug") = level match {
    case "info" => logger.info(msg)
    case "debug" => logger.debug(msg)
    case "error" => logger.error(msg)
    case _ =>
  }
}
