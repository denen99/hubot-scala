package org.dberg.hubot

import org.dberg.hubot.SpecHelpers._
import org.dberg.hubot.middleware.MiddlewareError
import org.dberg.hubot.models.{ Message, User }
import org.dberg.hubot.models.MessageType._
import org.scalatest.{ BeforeAndAfterAll, DoNotDiscover, Suites }

@DoNotDiscover
class HubotTestSuite extends SpecBase {

  val robot = hubot.robotService
  val brain = hubot.brainService

  val matchedMessage1 = Message(User("specuser"), "spectest message test", DirectMessage)

  "Hubot" should "have a name" in {
    assert(hubot.robotService.hubotName == "specbot")
  }

  "Hubot" should "receive a valid message" in {
    val resp = generateListenerResponse(matchedMessage1, "message test")
    (hubot.adapter.send _).expects(resp)
    robot.receive(matchedMessage1)
    val last = brain.get[String]("lastmessage").getOrElse("")
    assert(last == resp.body)
  }

  "Middleware" should "return an error with a bad message" in {
    val result = robot.processMiddleware(badMessage)
    assert(result == Left(MiddlewareError("Sorry this is blocked")))
  }

  "Middleware" should "return an error even when listener matches " in {
    val result = robot.processMiddleware(badMessage.copy(body = badMessage.body + "spectest"))
    assert(result == Left(MiddlewareError("Sorry this is blocked")))
  }

}
