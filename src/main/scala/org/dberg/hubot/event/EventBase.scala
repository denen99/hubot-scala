package org.dberg.hubot.event

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.Hubot

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Event(id: String, payload: Map[String, Any])

/*
   Here is an abstract class to extend to write a "callback" class.
   Each class simply implements a receive method that can check on
   the ID passed
 */
abstract class EventCallback(val hubot: Hubot) {
  val brain = hubot.brainService
  def receive(event: Event)
}

class TestCallback(hubot: Hubot) extends EventCallback(hubot) with StrictLogging {
  def receive(event: Event) = event.id match {
    case "testid" => logger.debug("Found test id ")
    case x => logger.debug("Not using emit value of " + x + " with event " + event)
  }
}

/*
  Basic Event Component to insert into Hubot.  Hubot can simply now do
  val eventService = new EventService
  eventService.emit(event) 
 */
trait EventComponent {

  def eventService: EventService
  def eventCallbacks: Seq[EventCallback]

  class EventService extends StrictLogging {

    def emit(event: Event) = eventCallbacks.map(c => Future { c.receive(event) })

  }

}

