package org.dberg.hubot.models


import java.util.regex.{Matcher, Pattern}

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
                         val hubot: Hubot,
                         matcher: String,
                         listenerType: ListenerValue = ListenerType.Respond
)  {

  //val robot = Robot.robotService
  val pattern = Pattern.compile(matcher)
  val robot = hubot.robotService
  val brain = hubot.brainService


  def buildGroups(matcher: Matcher, count: Int , results: Seq[String] = Seq()): Seq[String] = count match {
    case 0 => results
    case x => buildGroups(matcher,count-1,results :+ matcher.group(count))
  }

  def call(message: Message): Unit = {
    if (shouldRespond(message) ) {
      val matcher = pattern.matcher(message.body.removeBotString(robot.hubotName))
      matcher.find() match {
        case false => Logger.log("no match for listener " + this.getClass.getName)
        case true =>
          val groups = buildGroups(matcher,matcher.groupCount())
          runCallback(message.copy(
          body = message.body.removeBotString(robot.hubotName)
        ), groups)
      }
    }
    else { Logger.log("Sorry, listeners says we should not respond")}
  }

  def shouldRespond(message: Message): Boolean = {
    println(s"message $message $listenerType ${message.body} $robot.hubotName")
    listenerType == ListenerType.Hear || (listenerType ==  ListenerType.Respond && message.body.addressedToHubot(message,robot.hubotName))
  }

  def runCallback(message: Message, groups: Seq[String]): Unit

  def helpString: Option[String]
}


//-------------------------------------
// SOME TEST LISTENERS FOR NOW
//-------------------------------------
class TestListener(hubot: Hubot ) extends Listener(hubot,  "listen1\\s+(.*)", ListenerType.Hear) {

  def runCallback(message: Message, groups: Seq[String]) = {
    val resp = "scalabot heard you mention " + groups.head + " !"
    Logger.log("Running callback for listener TestListener, sending response " + resp,"debug")
    hubot.robotService.send(Message(message.user,resp, message.messageType))
  }

  val helpString = Some("listen1 -> Responds to anything and repeats it ")
}

class TestListener2(hubot: Hubot ) extends Listener(hubot,"listen2") {

  def runCallback(message: Message, groups: Seq[String]) = {
    Logger.log("Running callback for listener TestListener2","debug")
    hubot.robotService.send(Message(message.user,message.body.reverse, message.messageType))
  }

  val helpString = Some("listen2 -> reverses anything you send it ")

}

class HelpListener(hubot: Hubot) extends Listener(hubot,"^help") {

  val helpCommands = hubot.listeners.map(l => l.helpString).filter(l => l.isDefined)

  def runCallback(message: Message, groups: Seq[String]) = {
    Logger.log("Running help listener","debug")
    hubot.robotService.send(Message(message.user,"Help commands \n" + helpCommands.mkString("\n"),message.messageType))
  }

  val helpString = Some("help -> list all available commands ")
}
