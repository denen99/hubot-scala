package org.dberg.hubot

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.models.MessageType.DirectMessage
import org.dberg.hubot.models.{ Message, User }
import org.scalamock.scalatest.MockFactory
import org.scalatest.{ BeforeAndAfterAll, FlatSpec, Matchers }
import scodec.codecs.ImplicitCodecs

abstract class SpecBase extends FlatSpec with Matchers with MockFactory with BeforeAndAfterAll with StrictLogging with ImplicitCodecs {
  //  val hubot = new Hubot
  //  val brain = hubot.brainService
  //  val robot = hubot.robotService
  //  val event = hubot.eventService

  val badMessage = Message(User("specuser"), "blacklist", DirectMessage)
  val matchedMessage1 = Message(User("specuser"), "spectest param1", DirectMessage)

}