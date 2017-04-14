package org.dberg.hubot.adapter

import org.dberg.hubot.HubotBase
import org.dberg.hubot.models.{ Message, MessageType, User }

class SpecAdapter(hubot: HubotBase) extends BaseAdapter(hubot) {

  def send(message: Message) = {}

  def run() = {}
}