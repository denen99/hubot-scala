package org.dberg.hubot.adapter

import java.util.regex.Pattern
import javax.net.ssl.{SSLSession, HostnameVerifier}

import org.dberg.hubot.Hubot
import org.dberg.hubot.utils.Logger
import org.jivesoftware.smack.{XMPPConnection, ConnectionListener, ConnectionConfiguration}
import org.jivesoftware.smack.chat.{ChatManagerListener, ChatManager, Chat, ChatMessageListener}
import org.jivesoftware.smack.tcp.{XMPPTCPConnection, XMPPTCPConnectionConfiguration}
import org.dberg.hubot.models.{User, Message}
import org.dberg.hubot.utils.Helpers._


class HipchatAdapter extends BaseAdapter {

  val regex = "(^[^/]+)"
  val pattern = Pattern.compile(regex)

  type HubotMessage = org.dberg.hubot.models.Message
  type SmackMessage = org.jivesoftware.smack.packet.Message

  class chatListener extends ChatMessageListener {

    def getJid(from: String): String = {
      val matcher = pattern.matcher(from)
      if (matcher.find())
        matcher.group(1)
      else
        "Invalid JID"
    }

    def processMessage(chat: Chat, msg: SmackMessage) = {
      if (msg.getBody != null) {
        val jid = getJid(msg.getFrom)
        val user = User(jid)
        Hubot.robot.receive(Message(user, msg.getBody))
      }
    }
  }


  class connectListener extends ConnectionListener {
    def connected(connection: XMPPConnection) =
      Logger.log("Received connection from user : " + connection.getUser,"debug")

    def reconnectionFailed(e: Exception) =
     Logger.log("Connection failed : " + e.getMessage,"error")

    def reconnectionSuccessful =
     Logger.log("Reconnection successful","debug")

    def authenticated(connection: XMPPConnection, resumed: Boolean) =
     Logger.log("Authenticated Successful","info")

    def connectionClosedOnError(e: Exception) = {
      Logger.log("Connection closed : " + e.getMessage + ", attempting to re-connect","info")
      conn.removeConnectionListener(connectListener)
      conn.disconnect()
      run()
    }

    def connectionClosed =
     Logger.log("Error, connection closed","debug")

    def reconnectingIn(seconds: Int) =
     Logger.log("Reconnecting in " + seconds.toString + " seconds","debug")
  }

  class chatMgrListener extends ChatManagerListener {
    def chatCreated(chat: Chat, createdLocally: Boolean) = {
      if (!createdLocally) {
        chat.addMessageListener(new chatListener)
      }
    }

  }

  val jid = getConfString("hubot.jid","none")
  val password = getConfString("hubot.password","none")

  val verify = new HostnameVerifier {
    override def verify(s: String, sslSession: SSLSession): Boolean = true
  }

  val conf = XMPPTCPConnectionConfiguration.builder()
    .setServiceName("chat.hipchat.com")
    .setHost("conf.hipchat.com")
    .setPort(5222)
    .setDebuggerEnabled(false)
    .setHostnameVerifier(verify)
    .setUsernameAndPassword(jid, password)
    .setSecurityMode(ConnectionConfiguration.SecurityMode.required)

  val conn = new XMPPTCPConnection(conf.build())
  val chatMgr = ChatManager.getInstanceFor(conn)
  val connectListener = new connectListener
  chatMgr.addChatListener(new chatMgrListener)

  def run() = {

    conn.addConnectionListener(connectListener)
    if (!conn.isConnected)
      conn.connect()
    if (!conn.isAuthenticated)
      conn.login()
    if (conn.isAuthenticated) {
      while (conn.isAuthenticated) {
         // do nothing
      }
      Logger.log("Error - disconnected from server, reconnecting","error")
      run()
    }
  }

  def send(message: HubotMessage) = {
    val chat = chatMgr.createChat(message.user.room, new chatListener)
    chat.sendMessage(message.body)
  }


}