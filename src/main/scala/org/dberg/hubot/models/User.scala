package org.dberg.hubot.models

class User private(val id: Int,  val room: String, val dict: Map[String,String] = Map())

object User {
  def apply(id: Int, room: String, dict: Map[String,String] = Map()) =
    new User(id, room, dict + ("name" -> id.toString))
}


