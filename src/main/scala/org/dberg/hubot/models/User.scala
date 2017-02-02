package org.dberg.hubot.models

sealed abstract case class User private(
  room: String,
  id: Int = 1 ,
  dict: Map[String,String] = Map()
)

object User {
  def apply(room: String, id: Int = 1, dict: Map[String,String] = Map()) =
    new User(room, id, dict + ("name" -> id.toString)) {}
}


