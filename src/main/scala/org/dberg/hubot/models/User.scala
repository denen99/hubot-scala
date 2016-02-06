package org.dberg.hubot.models

class User private(val room: String, val id: Int = 1 , val dict: Map[String,String] = Map())

object User {
  def apply(room: String, id: Int = 1, dict: Map[String,String] = Map()) =
    new User(room, id, dict + ("name" -> id.toString))
}


