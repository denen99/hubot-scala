package org.dberg.hubot

import org.dberg.hubot.adapter.SpecAdapter
import org.dberg.hubot.listeners.{ Listener, SpecListener }
import org.dberg.hubot.middleware.{ Middleware, SpecMiddleware }
import org.dberg.hubot.models.{ Message, User }
import org.dberg.hubot.models.MessageType.DirectMessage
import org.scalatest.DoNotDiscover

@DoNotDiscover
class ListenerTestSuite extends SpecBase {

  val matchedMessage1 = Message(User("specuser"), "spectest", DirectMessage)

  class MockedHubot extends HubotBase {

    class MockAdapter extends SpecAdapter(this)
    class MockListener extends SpecListener(this)
    val mockListener = mock[MockListener]

    val brainService = new BrainService
    val robotService = new RobotService
    val eventService = new EventService

    val listeners: Seq[Listener] = Seq(new SpecListener(this), mockListener)
    val middleware: List[Middleware] = List(new SpecMiddleware(this))
    val adapter = mock[MockAdapter]

    val eventCallbacks = Seq()

  }

  "A listener" should "receive the matched regex groups" in {
    val mockedHubot = new MockedHubot
    val responseMessage1 = Message(User("specuser"), "received", DirectMessage)
    val param = "param1"
    //expect the message to be sent to the adapter
    (mockedHubot.adapter.send _).expects(responseMessage1.copy(body = responseMessage1.body + " " + param))
    (mockedHubot.mockListener.call _).expects(matchedMessage1.copy(body = matchedMessage1.body + " " + param))
    //Receive the message and the listener should get called
    mockedHubot.robotService.receive(matchedMessage1.copy(body = matchedMessage1.body + " " + param))
  }

  "A listener" should "get triggered when the regex matches" in {
    val mockedHubot = new MockedHubot
    val responseMessage1 = Message(User("specuser"), "received", DirectMessage)
    (mockedHubot.adapter.send _).expects(responseMessage1)
    (mockedHubot.mockListener.call _).expects(matchedMessage1)
    mockedHubot.robotService.receive(matchedMessage1)
  }

  "A listener" should "not run if the middleware blocks it" in {
    val mockedHubot = new MockedHubot

    (mockedHubot.adapter.send _).expects(*).never()
    (mockedHubot.mockListener.call _).expects(*).never()
    mockedHubot.robotService.receive(matchedMessage1.copy(body = matchedMessage1.body + " blacklist"))
  }
}
