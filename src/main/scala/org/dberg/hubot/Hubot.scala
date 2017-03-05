package org.dberg.hubot

import org.dberg.hubot.adapter.{BaseAdapter, HipchatAdapter, ShellAdapter}
import org.dberg.hubot.brain.BrainComponent
import org.dberg.hubot.middleware.{Middleware, MiddlewareError, MiddlewareSuccess, TestMiddleware}
import org.dberg.hubot.models._
import org.dberg.hubot.robot.RobotComponent
import org.dberg.hubot.utils.Helpers.{getConfString, getConfStringList}
import org.dberg.hubot.utils.Logger


class Hubot extends RobotComponent with BrainComponent {

  val robotService = new RobotService
  val brainService = new BrainService


  val listeners: Seq[Listener] = {
    getConfStringList("hubot.listeners").map({
      case "test" =>
        Logger.log("Registering listener: test","debug")
        new TestListener(this)
      case "test2" =>
        Logger.log("Registering listener: test2","debug")
        new TestListener2(this)
      case "help" =>
        Logger.log("Registering listener: help","debug")
        new HelpListener(this)
      case x =>
        Logger.log("Sorry, unknown listener " + x,"debug")
        throw new Exception("Invalid listener in configuration, " + x.toString)
    })
  }
  
  val adapter: BaseAdapter = {
    val a = getConfString("hubot.adapter","shell")
    a match {
      case "shell" => new ShellAdapter(this)
      case "hipchat" => new HipchatAdapter(this)
    }
  }

  val middleware = {
    getConfStringList("hubot.middleware").map({
      case "test" => TestMiddleware
      case x => Logger.log("Sorry, uknown middleware in config " + x,"debug")
    }).asInstanceOf[Seq[Middleware]]
  }
  

}

