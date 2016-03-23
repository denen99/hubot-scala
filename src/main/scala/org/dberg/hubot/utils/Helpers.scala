package org.dberg.hubot.utils

import com.typesafe.config.ConfigFactory
import org.dberg.hubot.Hubot
import scala.util.matching.Regex
import scala.collection.JavaConversions._


object Helpers {

  val regex: Regex = s"(?i)^[@]?${Hubot.robot.name}".r
  val regexStr = s"(?i)^[@]?${Hubot.robot.name}\\s*"

  implicit class robotMatcher(body: String) {

    def addressedToHubot: Boolean =
       regex.findFirstIn(body).isDefined

    def removeBotString: String =
      body.replaceFirst(regexStr,"")

  }

  val config = ConfigFactory.load()

  def getConfString(key: String, default: String): String = config.hasPath(key) match {
    case false =>  default
    case true => config.getString(key)
  }

  def getConfStringList(key: String): Seq[String] = config.hasPath(key) match {
    case false => Seq()
    case true => config.getStringList(key)
  }

}
