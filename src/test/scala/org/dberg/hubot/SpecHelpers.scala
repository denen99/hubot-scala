package org.dberg.hubot

import org.dberg.hubot.models.{ Message, User }

object SpecHelpers {

  //Just a helper method to abstract out the response a
  //listener generates for our tests 
  def generateListenerResponse(message: Message, param: String) = {
    if (param.isEmpty)
      Message(message.user, "received", message.messageType)
    else
      Message(message.user, "received " + param, message.messageType)
  }

}
