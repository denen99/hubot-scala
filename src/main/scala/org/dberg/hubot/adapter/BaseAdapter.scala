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


class TestAdapter extends BaseAdapter {

  def send(message: Message) =
    println(message.body)

  def run() = {
    Logger.log("Running adapter " + this.getClass.getName,"info")
    while(true) {
      print(Hubot.robot.name + " >")
      val resp = scala.io.StdIn.readLine()
      Hubot.robot.receive(Message(User(1,"adam"),resp))
    }
  }
}
