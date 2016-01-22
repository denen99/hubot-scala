package org.dberg.hubot.models


abstract class MessageBase(user: User, body: String, done: Boolean = false ) {
  val room = user.room
}

case class Message(user: User,  body: String, done: Boolean = false ) extends MessageBase(user,body)

case class TextMessage(user: User, body: String, id: Int) extends MessageBase(user,body)
