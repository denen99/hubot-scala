package org.dberg.hubot

import java.awt.TrayIcon.MessageType

import org.dberg.hubot.brain.MapdbBackend
import org.dberg.hubot.middleware.MiddlewareError
import org.dberg.hubot.models.{ Message, User }
import org.dberg.hubot.models.MessageType._
import org.scalatest.{ BeforeAndAfterAll, DoNotDiscover, Suites }

@DoNotDiscover
class HubotTestSuite extends SpecBase {

  //  "Hubot" should "have a name" in {
  //    assert(hubot.robotService.hubotName == "specbot")
  //  }
  //
  //  "Hubot" should "receive a valid message" in {
  //    val message = "spectest message test"
  //    robot.receive(Message(User("spec"), message, DirectMessage))
  //    val last = brain.get[String]("lastmessage").getOrElse("")
  //    assert(last == message)
  //  }
  //
  //  "Middleware" should "return an error with a bad message" in {
  //    val result = robot.processMiddleware(badMessage)
  //    assert(result == Left(MiddlewareError("Sorry this is blocked")))
  //  }
  //
  //  "Middleware" should "return an error even when listener matches " in {
  //    val result = robot.processMiddleware(badMessage.copy(body = badMessage.body + "spectest"))
  //    assert(result == Left(MiddlewareError("Sorry this is blocked")))
  //  }

}
