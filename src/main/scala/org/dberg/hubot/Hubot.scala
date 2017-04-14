package org.dberg.hubot

import org.dberg.hubot.adapter.{ BaseAdapter, HipchatAdapter, ShellAdapter }
import org.dberg.hubot.brain.BrainComponent
import org.dberg.hubot.middleware.{ Middleware, MiddlewareError, MiddlewareSuccess, TestMiddleware }
import org.dberg.hubot.models._
import org.dberg.hubot.utils.Helpers.{ getConfString, getConfStringList }
import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.event.{ EventCallback, EventComponent }
import org.dberg.hubot.listeners.Listener
import org.dberg.hubot.robot.RobotComponent

trait HubotBase extends RobotComponent with BrainComponent with EventComponent {
  def robotService: RobotService
  def brainService: BrainService
  def eventService: EventService

  def listeners: Seq[Listener]
  def middleware: List[Middleware]
  def adapter: BaseAdapter
  def eventCallbacks: Seq[EventCallback]
}

class Hubot extends HubotBase with StrictLogging {

  logger.debug("Creating Robot Service")
  val robotService = new RobotService

  logger.debug("Creating Brain Service")
  val brainService = new BrainService

  logger.debug("Creating Event Service")
  val eventService = new EventService

  val listeners: Seq[Listener] = {
    ("org.dberg.hubot.listeners.HelpListener" +: getConfStringList("hubot.listeners")).map({ l =>
      logger.debug("Registering listener " + l)
      val c = Class.forName(l).getConstructor(this.getClass)
      c.newInstance(this).asInstanceOf[Listener]
    })
  }

  val adapter: BaseAdapter = {
    val a = getConfString("hubot.adapter", "org.dberg.hubot.adapter.ShellAdapter")
    logger.debug("Registering adapter " + a)
    val c = Class.forName(a).getConstructor(this.getClass)
    c.newInstance(this).asInstanceOf[BaseAdapter]
  }

  val middleware = {
    getConfStringList("hubot.middleware").map({ m =>
      logger.debug("Registering middleware " + m)
      val c = Class.forName(m).getConstructor(this.getClass)
      c.newInstance(this).asInstanceOf[Middleware]
    }).toList
  }

  val eventCallbacks: Seq[EventCallback] = getConfStringList("hubot.eventCallbacks").map({ e =>
    logger.debug("Registering event Callback " + e)
    val c = Class.forName(e).getConstructor(this.getClass)
    c.newInstance(this).asInstanceOf[EventCallback]
  })

}

