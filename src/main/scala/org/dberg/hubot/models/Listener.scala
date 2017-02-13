package org.dberg.hubot.models

import java.util.regex.Pattern

import org.dberg.hubot.Hubot
import org.dberg.hubot.utils.Logger
import org.dberg.hubot.models.ListenerType.ListenerValue
import org.dberg.hubot.utils.Helpers._
import org.dberg.hubot.models.Robot.RobotService

object ListenerType {
  sealed trait ListenerValue
  case object Respond extends ListenerValue
  case object Hear extends ListenerValue
}


abstract class Listener(
  robot: RobotService,
  matcher: String,
  listenerType: ListenerValue = ListenerType.Respond
)  {

  //val robot = Robot.robotService
  val pattern = matcher.r
  val robotName = robot.hubotName

  def call(message: Message): Unit = {
    if (shouldRespond(message) ) {
      pattern.findFirstIn(message.body.removeBotString(robotName)) match {
        case None => Logger.log("no match for listner " + this.getClass.getName)
        case Some(x) => runCallback(message.copy(
          body = message.body.removeBotString(robotName)
        ))
      }
    }
    else { Logger.log("Sorry, listeners says we should not respond")}
  }

  def shouldRespond(message: Message): Boolean = {
    println(s"message $message $listenerType ${message.body} $robotName")
    listenerType == ListenerType.Hear || (listenerType ==  ListenerType.Respond && message.body.addressedToHubot(robotName))
  }

  def runCallback(message: Message): Unit

  def helpString: Option[String]
}


//-------------------------------------
// SOME TEST LISTENERS FOR NOW
//-------------------------------------
class TestListener(robot: RobotService) extends Listener(robot, "listen1\\s+", ListenerType.Hear) {

  def runCallback(message: Message) = {
    val resp = "listen1 heard " + message.body
    Logger.log("Running callback for listner TestListener, sending response " + resp,"debug")
    robot.send(Message(message.user,resp))
  }

  val helpString = Some("listen1 -> Responds to anything and repeats it ")
}

class TestListener2(robot: RobotService) extends Listener(robot,"listen2") {

  def runCallback(message: Message) = {
    Logger.log("Running callback for listner TestListener2","debug")
    robot.send(Message(message.user,message.body.reverse))
  }

  val helpString = Some("listen2 -> reverses anything you send it ")

}

class HelpListener(robot: RobotService) extends Listener(robot,"^help") {

  val helpCommands = Seq("help", "help2")

  def runCallback(message: Message) = {
    Logger.log("Running help listener","debug")
    robot.send(Message(message.user,"Help commands \n" + helpCommands.mkString("\n")))
  }

  val helpString = Some("help -> list all available commands ")
}
