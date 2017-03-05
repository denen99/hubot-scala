package org.dberg.hubot.brain

trait BrainBackendBase {
  def setKey(key: String, value: String): Unit
  def getKey(key: String): Any
  def shutdown(): Unit
}