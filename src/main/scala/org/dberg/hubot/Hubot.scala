package org.dberg.hubot

import org.dberg.hubot.adapter.{ BaseAdapter, HipchatAdapter, ShellAdapter }
import org.dberg.hubot.brain.BrainComponent
import org.dberg.hubot.middleware.{ Middleware, MiddlewareError, MiddlewareSuccess, TestMiddleware }
import org.dberg.hubot.models._
import org.dberg.hubot.robot.RobotComponent
import org.dberg.hubot.utils.Helpers.{ getConfString, getConfStringList }
import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.event.{ EventCallback, EventComponent }

class Hubot extends RobotComponent with BrainComponent with EventComponent with StrictLogging {

  val robotService = new RobotService
  val brainService = new BrainService
  val eventService = new EventService

  val listeners: Seq[Listener] = {
    getConfStringList("hubot.listeners").map({ l =>
      logger.debug("Registering listener " + l)
      val c = Class.forName(l).getConstructor(this.getClass)
      c.newInstance(this).asInstanceOf[Listener]
    })
  }

  val adapter: BaseAdapter = {
    val a = getConfString("hubot.adapter", "org.dberg.hubot.adapter.ShellAdapter")
    val c = Class.forName(a).getConstructor(this.getClass)
    c.newInstance(this).asInstanceOf[BaseAdapter]
  }

  val middleware = {
    getConfStringList("hubot.middleware").map({ m =>
      logger.debug("Registering middleware " + m)
      val c = Class.forName(m).getConstructor(this.getClass)
      c.newInstance(this).asInstanceOf[Middleware]
    })
  }

  val eventCallbacks: Seq[EventCallback] = getConfStringList("hubot.eventCallbacks").map({ e =>
    logger.debug("Registering event Callback " + e)
    val c = Class.forName(e).getConstructor(this.getClass)
    c.newInstance(this).asInstanceOf[EventCallback]
  })

}

