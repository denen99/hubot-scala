package org.dberg.hubot.models

sealed trait MessageType

object MessageType {
  case object DirectMessage extends MessageType
  case object GroupMessage extends MessageType
}

abstract class MessageBase(user: User, body: String, messageType: MessageType, params: Map[String, String] = Map()) {
  val room = user.room

}

final case class Message(
  user: User,
  body: String,
  messageType: MessageType,
  params: Map[String, String] = Map(),
  done: Boolean = false
) extends MessageBase(user, body, messageType)

object Body {
  def unapply(message: Message): Option[String] =
    Some(message.body)
}

object & {
  def unapply[A](value: A): Option[(A, A)] = Some(value, value)
}

object SentBy {
  def unapply(message: Message): Option[User] =
    Some(message.user)
}

object Direct {
  def unapply(message: Message): Boolean =
    message.messageType match {
      case MessageType.DirectMessage => true
      case _ => false
    }
}

object Group {
  def unapply(message: Message): Boolean =
    message.messageType match {
      case MessageType.GroupMessage => true
      case _ => false
    }
}

object Room {
  def unapply(message: Message): Option[String] =
    Some(message.user.room)
}

