package org.dberg.hubot.middleware

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.Hubot
import org.dberg.hubot.models.Message

class SpecMiddleware(hubot: Hubot) extends Middleware(hubot) with StrictLogging {

  def execute(message: Message) = {
    if (message.body == "blacklist") {
      Left(MiddlewareError("Sorry this is blocked"))
    } else {
      Right(MiddlewareSuccess())
    }
  }
}