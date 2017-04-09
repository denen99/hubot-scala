package org.dberg.hubot.adapter

import org.dberg.hubot.Hubot
import org.dberg.hubot.models.{ Message, MessageType, User }

class SpecAdapter(hubot: Hubot) extends BaseAdapter(hubot) {

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