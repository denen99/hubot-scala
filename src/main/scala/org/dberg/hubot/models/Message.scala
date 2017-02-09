package org.dberg.hubot.models

import org.dberg.hubot.Hubot


abstract class MessageBase(user: User, body: String,  params: Map[String,String] = Map()) {
  val room = user.room
  val robot = Robot.robotService

  def respondTo = {
    val regex = s"""^${robot.hubotName}"""
  }
}

final case class Message(
  user: User,
  body: String,
  params: Map[String,String] = Map(),
  done: Boolean = false
) extends MessageBase(user,body)
