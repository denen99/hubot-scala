package org.dberg.hubot.models

import org.dberg.hubot.Hubot


abstract class MessageBase(user: User, body: String, done: Boolean = false ) {
  val room = user.room

  def respondTo = {
    val regex = s"""^${Hubot.robot.name}"""
  }
}

case class Message(user: User,  body: String, done: Boolean = false ) extends MessageBase(user,body)

case class TextMessage(user: User, body: String, id: Int) extends MessageBase(user,body)
