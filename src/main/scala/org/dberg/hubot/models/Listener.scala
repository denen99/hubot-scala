package org.dberg.hubot.models

import java.util.regex.Pattern

import org.dberg.hubot.Hubot
import org.dberg.hubot.logger.Logger
import org.slf4j.impl.StaticLoggerBinder

abstract class Listener(matcher: String, option: Map[String,String] = Map())  {

  def call(message: Message): Unit = {
    val pattern = matcher.r
    pattern.findFirstIn(message.body) match {
      case None => Logger.log("no match")
      case Some(x) => runCallback(message)
    }
  }

  def runCallback(message: Message): Unit

}

case class TestListener(matcher: String, option: Map[String,String] = Map()) extends Listener(matcher,option) {

  def runCallback(message: Message) = {
    Hubot.robot.send(Message(message.user,"Received message " + message.body))
  }
}

case class TestListener2() extends Listener(".*") {

  def runCallback(message: Message) =
    Hubot.robot.send(Message(message.user,message.body.reverse))
}
