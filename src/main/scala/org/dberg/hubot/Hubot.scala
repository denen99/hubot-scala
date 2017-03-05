package org.dberg.hubot

import org.dberg.hubot.adapter.{BaseAdapter, HipchatAdapter, ShellAdapter}
import org.dberg.hubot.brain.BrainComponent
import org.dberg.hubot.middleware.{Middleware, MiddlewareError, MiddlewareSuccess, TestMiddleware}
import org.dberg.hubot.models._
import org.dberg.hubot.robot.RobotComponent
import org.dberg.hubot.utils.Helpers.{getConfString, getConfStringList}
import com.typesafe.scalalogging.StrictLogging

class Hubot extends RobotComponent with BrainComponent with StrictLogging {

  val robotService = new RobotService
  val brainService = new BrainService


  val listeners: Seq[Listener] = {
    getConfStringList("hubot.listeners").map({
      case "test" =>
        logger.debug("Registering listener: test")
        new TestListener(this)
      case "test2" =>
        logger.debug("Registering listener: test2")
        new TestListener2(this)
      case "help" =>
        logger.debug("Registering listener: help")
        new HelpListener(this)
      case x =>
        logger.debug("Sorry, unknown listener " + x)
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
      case x => logger.info("Sorry, uknown middleware in config " + x,"debug")
    }).asInstanceOf[Seq[Middleware]]
  }


}

