package org.dberg.hubot

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.adapter.SpecAdapter
import org.dberg.hubot.listeners.{ Listener, SpecListener }
import org.dberg.hubot.middleware.{ Middleware, SpecMiddleware }
import org.dberg.hubot.models.MessageType.Direct
import org.dberg.hubot.models.{ Message, User }
import org.scalamock.scalatest.MockFactory
import org.scalatest.{ BeforeAndAfterAll, FlatSpec, Matchers }
import scodec.codecs.ImplicitCodecs

abstract class SpecBase extends FlatSpec with Matchers with MockFactory with BeforeAndAfterAll with StrictLogging with ImplicitCodecs {

  val badMessage = Message(User("specuser"), "blacklist", Direct)

  class TestHubot extends HubotBase {

    class MockedAdapter extends SpecAdapter(this)
    val brainService = new BrainService
    val robotService = new RobotService
    val eventService = new EventService

    val listeners: Seq[Listener] = Seq(new SpecListener(this))
    val middleware: List[Middleware] = List(new SpecMiddleware(this))
    val adapter = mock[MockedAdapter]

    val eventCallbacks = Seq()
  }

  val hubot = new TestHubot

}