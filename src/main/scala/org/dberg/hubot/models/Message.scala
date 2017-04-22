package org.dberg.hubot.models

sealed trait MessageType

object MessageType {
  case object Direct extends MessageType {
    def unapply(message: Message) = message.messageType match {
      case Direct => true
      case _ => false
    }
  }
  case object Group extends MessageType {
    def unapply(message: Message) = message.messageType match {
      case Group => true
      case _ => false
    }
  }
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

object Room {
  def unapply(message: Message): Option[String] =
    Some(message.user.room)
}

