package org.dberg.hubot.models

import java.util.regex.Pattern

import org.dberg.hubot.Hubot
import org.dberg.hubot.utils.Logger
import org.dberg.hubot.models.ListenerType.ListenerValue
import org.dberg.hubot.utils.Helpers._

object ListenerType {
  sealed trait ListenerValue
  case object Respond extends ListenerValue
  case object Hear extends ListenerValue
}


abstract class Listener(
  robotName: String,
  matcher: String,
  listenerType: ListenerValue = ListenerType.Respond
)  {

  val pattern = matcher.r

  def call(robot: Robot, message: Message): Unit = {
    if (shouldRespond(message) ) {
      pattern.findFirstIn(message.body.removeBotString(robotName)) match {
        case None => Logger.log("no match for listner " + this.getClass.getName)
        case Some(x) => runCallback(robot, message.copy(
          body = message.body.removeBotString(robotName)
        ))
      }
    }
  }

  def shouldRespond(message: Message): Boolean = {
    println(s"message $message $listenerType ${message.body} $robotName")
    listenerType == ListenerType.Hear || (listenerType ==  ListenerType.Respond && message.body.addressedToHubot(robotName))
  }

  def runCallback(robot: Robot, message: Message): Unit

  def helpString: Option[String]
}


//-------------------------------------
// SOME TEST LISTENERS FOR NOW
//-------------------------------------
case class TestListener(robotName: String) extends Listener(robotName, "listen1\\s+") {

  def runCallback(robot: Robot, message: Message) = {
    val resp = "listen1 heard " + message.body
    Logger.log("Running callback for listner TestListener, sending response " + resp,"debug")
    robot.send(Message(message.user,resp))
  }

  val helpString = Some("listen1 -> Responds to anything and repeats it ")
}

case class TestListener2(robotName: String) extends Listener(robotName, "listen2") {

  def runCallback(robot: Robot, message: Message) = {
    Logger.log("Running callback for listner TestListener2","debug")
    robot.send(Message(message.user,message.body.reverse))
  }

  val helpString = Some("listen2 -> reverses anything you send it ")

}

case class HelpListener(robotName: String, helpCommands: Seq[String]) extends Listener(robotName, "^help") {

  def runCallback(robot: Robot, message: Message) = {
    Logger.log("Running help listener","debug")
    robot.send(Message(message.user,"Help commands \n" + helpCommands.mkString("\n")))
  }

  val helpString = Some("help -> list all available commands ")
}
