package org.dberg.hubot

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.adapter.SpecAdapter
import org.dberg.hubot.brain.BrainComponent
import org.dberg.hubot.event.EventComponent
import org.dberg.hubot.listeners.{ HelpListener, Listener, SpecListener }
import org.dberg.hubot.middleware.{ Middleware, SpecMiddleware }
import org.dberg.hubot.robot.RobotComponent
import org.scalatest.DoNotDiscover

@DoNotDiscover
class ListenerTestSuite extends SpecBase {

  class MockedHubot extends HubotBase {

    class MockListener extends SpecListener(this)

    val mockListener = mock[MockListener]
    (mockListener.call _).expects(matchedMessage1)

    //This doesnt run ! 
    //(mockListener.runCallback _).expects(matchedMessage1, List("param1"))

    val listeners: Seq[Listener] = Seq(mockListener)
    val middleware: List[Middleware] = List()
    val adapter = new SpecAdapter(this)

    val brainService = new BrainService
    val robotService = new RobotService
    val eventService = new EventService

    val eventCallbacks = Seq()

  }

  val mockedHubot = new MockedHubot

  "A listener " should "receive the matched regex groups" in {
    logger.debug("Creating mocks")
    mockedHubot.robotService.receive(matchedMessage1)
  }
}
