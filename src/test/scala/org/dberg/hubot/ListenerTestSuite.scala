package org.dberg.hubot

import org.dberg.hubot.SpecHelpers._
import org.dberg.hubot.listeners.Listener._
import org.dberg.hubot.models.{ Message, User }
import org.dberg.hubot.models.MessageType.{ Direct, Group }
import org.scalatest.DoNotDiscover

@DoNotDiscover
class ListenerTestSuite extends SpecBase {

  val matchedMessage1 = Message(User("specuser"), "spectest", Direct)
  val matchedMessage2 = Message(User("specuser"), "spectest param1", Direct)
  val blacklistMessage = Message(User("specuser"), "spectest blacklist", Direct)
  val unMatchedDirectMessage = Message(User("specuser"), "nothing matches", Direct)
  val unMatchedGroupMessage = Message(User("specuser"), hubot.robotService.hubotName + " nothing matches", Group)
  val skippableGroupMessage = Message(User("specuser"), "nothing matches", Group)
  val failureMessage = Message(User("specuser"), "spectest failure", Direct)

  "A listener" should "receive the matched regex groups" in {
    val param = "param1"
    (hubot.adapter.send _).expects(generateListenerResponse(matchedMessage2, param))
    assert(hubot.robotService.receive(matchedMessage2) == Seq(CallbackSuccess))
  }

  it should "get triggered when the regex matches a DirectMessage" in {
    val param = ""
    (hubot.adapter.send _).expects(generateListenerResponse(matchedMessage1, param))
    assert(hubot.robotService.receive(matchedMessage1) == Seq(CallbackSuccess))
  }

  it should "not run if the middleware blocks it" in {
    (hubot.adapter.send _).expects(*).never()
    assert(hubot.robotService.receive(blacklistMessage) == Seq(CallbackMiddlewareFailure))
  }

  it should "respond if its a GroupMessage and addressed to hubot" in {
    val message = matchedMessage1.copy(body = hubot.robotService.hubotName + " " + matchedMessage1.body, messageType = Group)
    val param = ""
    val resp = generateListenerResponse(message, param)
    (hubot.adapter.send _).expects(resp)
    assert(hubot.robotService.receive(message) == Seq(CallbackSuccess))
  }

  it should "return CallbackNotMatched if no listener matches in a DirectMessage" in {
    (hubot.adapter.send _).expects(*).never()
    assert(hubot.robotService.receive(unMatchedDirectMessage) == Seq(CallbackNotMatched))
  }

  it should "return CallbackNotMatched if no listener matches in a GroupMessage" in {
    (hubot.adapter.send _).expects(*).never()
    assert(hubot.robotService.receive(unMatchedGroupMessage) == Seq(CallbackNotMatched))
  }

  it should "return CallbackSkipped if robot should not respond to a GroupMessage" in {
    (hubot.adapter.send _).expects(*).never()
    assert(hubot.robotService.receive(skippableGroupMessage) == Seq(CallbackSkipped))
  }

  it should "return a CallbackFailure if the runCallback fails" in {
    (hubot.adapter.send _).expects(*).never()
    assert(hubot.robotService.receive(failureMessage) == Seq(CallbackFailure(exception)))
  }

}
