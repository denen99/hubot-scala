package org.dberg.hubot

import org.dberg.hubot.models.Message

object SpecHelpers {

  val exception = new Exception("Dumb Exception")

  //Just a helper method to abstract out the response a
  //listener generates for our tests
  //Param is kind of redundanat since its in the body but helps
  //make this easy and generic
  def generateListenerResponse(message: Message, param: String) = {
    if (param.isEmpty)
      Message(message.user, "received", message.messageType)
    else
      Message(message.user, "received " + param, message.messageType)
  }

}
