package org.dberg.hubot.middleware

import org.dberg.hubot.models.Message

trait MiddlewareResponse
case class MiddlewareError(error: String) extends MiddlewareResponse
case class MiddlewareSuccess() extends MiddlewareResponse

abstract class Middleware() {

  def execute(message: Message): Either[MiddlewareError, MiddlewareSuccess]

}

case class TestMiddleware() extends Middleware {

  def execute(message: Message) = {
    if (message.body == "blacklist") {
      Left(MiddlewareError("Sorry this is a blacklist"))
    }
    else {
      Right(MiddlewareSuccess())
    }
  }
}