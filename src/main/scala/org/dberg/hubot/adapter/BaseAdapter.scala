package org.dberg.hubot.adapter

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.Hubot
import org.dberg.hubot.models.{ Message, MessageType, User }

abstract class BaseAdapter(hubot: Hubot) extends StrictLogging {

  val robot = hubot.robotService
  val brain = hubot.brainService
  val event = hubot.eventService

  def send(message: Message): Unit

  def run(): Unit

  def receive(message: Message): Unit = {
    logger.debug("Adapter received message : " + message)
    hubot.robotService.receive(message)
  }
}

case class ShellAdapter(hubot: Hubot) extends BaseAdapter(hubot: Hubot) {

  def send(message: Message) =
    println(message.body)

  def run() = {
    logger.info("Running adapter " + this.getClass.getName)

    while (true) {
      print(robot.hubotName + " >")
      Option(scala.io.StdIn.readLine())
        .map(_.trim)
        .filter(_.nonEmpty)
        .foreach { resp =>
          robot.receive(Message(User("adam"), resp, MessageType.GroupMessage))
        }
    }
  }
}
