package org.dberg.hubot.listeners

import org.dberg.hubot.HubotBase
import org.dberg.hubot.models._
import org.dberg.hubot.SpecHelpers._

class SpecListener(hubot: HubotBase) extends Listener(hubot) {

  val helpString = Some("this is a spec helper")

  val callback: Callback = {
    case message @ Body(r"spectest\s*(.*)$phrase\s*$$") =>
      logger.debug("running spec listener callback")
      val resp = generateListenerResponse(message, phrase)

      //Bogus data to simulate a failure
      if (message.body.contains("failure")) {
        throw exception
      } else {
        hubot.brainService.set[String]("lastmessage", resp.body)
        hubot.adapter.send(resp)
      }
  }
}
