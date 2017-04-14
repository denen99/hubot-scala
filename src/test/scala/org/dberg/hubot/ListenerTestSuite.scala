package org.dberg.hubot

import org.dberg.hubot.adapter.SpecAdapter
import org.dberg.hubot.SpecHelpers._
import org.dberg.hubot.listeners.{ Listener, SpecListener }
import org.dberg.hubot.middleware.{ Middleware, SpecMiddleware }
import org.dberg.hubot.models.{ Message, User }
import org.dberg.hubot.models.MessageType.{ DirectMessage, GroupMessage }
import org.scalatest.DoNotDiscover

@DoNotDiscover
class ListenerTestSuite extends SpecBase {

  val matchedMessage1 = Message(User("specuser"), "spectest", DirectMessage)
  val matchedMessage2 = Message(User("specuser"), "spectest param1", DirectMessage)
  val blacklistMessage = Message(User("specuser"), "spectest blacklist", DirectMessage)

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
    val param = "param1"
    //expect the message to be sent to the adapter and listener 
    (mockedHubot.mockListener.call _).expects(matchedMessage2)
    (mockedHubot.adapter.send _).expects(generateListenerResponse(matchedMessage2, param))

    //Receive the message and the listener should get called
    mockedHubot.robotService.receive(matchedMessage2)
  }

  "A listener" should "get triggered when the regex matches a DirectMessage" in {
    val mockedHubot = new MockedHubot
    val param = ""
    (mockedHubot.adapter.send _).expects(generateListenerResponse(matchedMessage1, param))
    (mockedHubot.mockListener.call _).expects(matchedMessage1)
    mockedHubot.robotService.receive(matchedMessage1)
  }

  "A listener" should "not run if the middleware blocks it" in {
    val mockedHubot = new MockedHubot
    (mockedHubot.adapter.send _).expects(*).never()
    (mockedHubot.mockListener.call _).expects(*).never()
    mockedHubot.robotService.receive(blacklistMessage)
  }

  "A listener" should "respond if its a GroupMessage and addressed to hubot" in {
    val mockedHubot = new MockedHubot
    val message = matchedMessage1.copy(body = mockedHubot.robotService.hubotName + " " + matchedMessage1.body, messageType = GroupMessage)
    val param = ""
    val resp = generateListenerResponse(message, param)
    (mockedHubot.adapter.send _).expects(resp)
    (mockedHubot.mockListener.call _).expects(message)
    mockedHubot.robotService.receive(message)
  }

}
