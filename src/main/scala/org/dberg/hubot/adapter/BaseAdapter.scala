package org.dberg.hubot.adapter

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.Hubot
import org.dberg.hubot.models.{ Message, MessageType, User }

abstract class BaseAdapter(val hubot: Hubot) extends StrictLogging {

  val robot = hubot.robotService
  val brain = hubot.brainService
  val event = hubot.eventService

  def send(message: Message): Unit

  def run(): Unit

  def receive(message: Message): Unit = {
    logger.debug("Adapter received message : " + message)
    robot.receive(message)
  }
}

