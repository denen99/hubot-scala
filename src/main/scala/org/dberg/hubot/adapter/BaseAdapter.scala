package org.dberg.hubot.adapter

import com.typesafe.scalalogging.{StrictLogging, LazyLogging}
import org.dberg.hubot.Hubot
import org.dberg.hubot.models.{User, Message, Robot}
import org.dberg.hubot.utils.Logger

abstract class BaseAdapter {

  def send(message: Message): Unit

  def run(): Unit

  def receive(message: Message): Unit = {
    Logger.log("Adapter received message : " + message,"debug")
    Hubot.robot.receive(message)
  }
}


class ShellAdapter extends BaseAdapter {

  def send(message: Message) =
    println(message.body)

  def run() = {
    Logger.log("Running adapter " + this.getClass.getName,"info")
    while(true) {
      print(Hubot.robot.name + " >")
      Option(scala.io.StdIn.readLine())
        .map(_.trim)
        .filter(_.nonEmpty)
        .foreach { resp =>
      Hubot.robot.receive(Message(User("adam"),resp))
      }
    }
  }
}
