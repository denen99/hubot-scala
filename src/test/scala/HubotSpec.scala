package org.dberg.hubot

import java.awt.TrayIcon.MessageType

import org.dberg.hubot.brain.MapdbBackend
import org.dberg.hubot.middleware.MiddlewareError
import org.dberg.hubot.models.{ Message, User }
import org.dberg.hubot.models.MessageType._
import org.scalatest.{ BeforeAndAfterAll, DoNotDiscover, Suites }

class MasterSpec extends Suites(new HubotSpec, new BrainSpec) with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    MapdbBackend.deleteAll()
  }

  override def afterAll(): Unit = {
    MapdbBackend.shutdown()
  }
}

@DoNotDiscover
class HubotSpec extends SpecBase {

  "Hubot" should "have a name" in {
    assert(hubot.robotService.hubotName == "specbot")
  }

  "Hubot" should "receive a valid message" in {
    val message = "spectest message test"
    robot.receive(Message(User("spec"), message, DirectMessage))
    val last = brain.get[String]("lastmessage").getOrElse("")
    assert(last == message)
  }

  "Middleware" should "return an error" in {
    val result = robot.processMiddleware(badMessage)
    robot.receive(badMessage)
    assert(result == Left(MiddlewareError("Sorry this is blocked")))
  }

  "Middleware" should "return an error even when listener matches " in {
    val result = robot.processMiddleware(badMessage.copy(body = badMessage.body + "spectest"))
    robot.receive(badMessage)
    assert(result == Left(MiddlewareError("Sorry this is blocked")))
  }

}
