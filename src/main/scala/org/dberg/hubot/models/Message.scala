package org.dberg.hubot.models

import org.dberg.hubot.models.MessageType.MessageTypeValue

object MessageType {
  sealed trait MessageTypeValue
  case object DirectMessage extends MessageTypeValue
  case object GroupMessage extends MessageTypeValue
}

abstract class MessageBase(user: User, body: String, messageType: MessageTypeValue, params: Map[String, String] = Map()) {
  val room = user.room

}

final case class Message(
  user: User,
  body: String,
  messageType: MessageTypeValue,
  params: Map[String, String] = Map(),
  done: Boolean = false
) extends MessageBase(user, body, messageType)
