package org.dberg.hubot.utils

import com.typesafe.config.ConfigFactory
import org.dberg.hubot.models.{Message, MessageType}

import scala.util.matching.Regex
import scala.collection.JavaConversions._


object Helpers {

  def regex(name: String): Regex = s"(?i)^[@]?$name".r
  def regexStr(name: String) = s"(?i)^[@]?$name\\s*"

  implicit class RobotMatcher(body: String) {

    def addressedToHubot(message: Message, hubotName: String): Boolean =
       message.messageType == MessageType.DirectMessage || regex(hubotName).findFirstIn(body).isDefined

    def removeBotString(name: String): String =
      body.replaceFirst(regexStr(name), "")

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
