package org.dberg.hubot.listeners

object ListenerType {
  sealed trait ListenerValue
  case object Respond extends ListenerValue
  case object Hear extends ListenerValue
}
