package org.dberg.hubot.adapter

import com.typesafe.scalalogging.{StrictLogging, LazyLogging}
import org.dberg.hubot.Hubot
import org.dberg.hubot.message.Envelope
import org.dberg.hubot.models.{User, Message, Robot}
import org.dberg.hubot.logger.Logger

abstract class BaseAdapter {


  def send(message: Message): Unit

  //def reply(e: Envelope, s: String*): Unit

  //def topic(e: Envelope, s: String*): Unit

  //def play(e: Envelope, s: String*): Unit

  def run(): Unit

  //def close(): Unit

  def receive(message: Message): Unit =
     Hubot.robot.receive(message)

//  def emote(e: Envelope, s: String*): Unit =
//     send(e,s)

}


class TestAdapter extends BaseAdapter {

  def send(message: Message) =
    println(message.user.room + " : " + message.body)

  def run() = {
    Logger.log("Running adapter" + this.getClass.getName)
    while(true) {
      print("hubot>")
      val resp = scala.io.StdIn.readLine()
      Hubot.robot.receive(Message(User(1,"adam"),resp))
    }
  }
}
