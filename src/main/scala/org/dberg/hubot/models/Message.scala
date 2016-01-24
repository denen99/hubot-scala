package org.dberg.hubot.models

import org.dberg.hubot.Hubot


abstract class MessageBase(user: User, body: String, params: Map[String,String] = Map() ) {
  val room = user.room

  def respondTo = {
    val regex = s"""^${Hubot.robot.name}"""
  }
}

case class Message(user: User,  body: String, params: Map[String,String] = Map(), done: Boolean = false ) extends MessageBase(user,body)
