package org.dberg.hubot.adapter


import org.dberg.hubot.Hubot
import org.dberg.hubot.models.{Message, MessageType, User}
import org.dberg.hubot.utils.Logger

abstract class BaseAdapter(hubot: Hubot) {

  def send(message: Message): Unit

  def run(): Unit

  def receive( message: Message): Unit = {
    Logger.log("Adapter received message : " + message,"debug")
    hubot.robotService.receive(message)
  }
}


case class ShellAdapter(hubot: Hubot) extends BaseAdapter(hubot: Hubot) {

  def send(message: Message) =
    println(message.body)

  def run() = {
    Logger.log("Running adapter " + this.getClass.getName,"info")
    while(true) {
      print(hubot.robotService.hubotName + " >")
      Option(scala.io.StdIn.readLine())
        .map(_.trim)
        .filter(_.nonEmpty)
        .foreach { resp =>
      hubot.robotService.receive(Message(User("adam"),resp, MessageType.GroupMessage))
      }
    }
  }
}
