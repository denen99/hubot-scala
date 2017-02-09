package org.dberg.hubot.adapter

import java.util.regex.Pattern
import javax.net.ssl.{HostnameVerifier, SSLSession}

import org.dberg.hubot.Hubot
import org.dberg.hubot.utils.Logger
import org.jivesoftware.smack.{ConnectionConfiguration, ConnectionListener, XMPPConnection}
import org.jivesoftware.smack.chat.{Chat, ChatManager, ChatManagerListener, ChatMessageListener}
import org.jivesoftware.smack.tcp.{XMPPTCPConnection, XMPPTCPConnectionConfiguration}
import org.dberg.hubot.models.{Message => HubotMessage, Robot, User}
import org.dberg.hubot.utils.Helpers._
import org.jivesoftware.smack.packet.{Message => SmackMessage}

object HipchatAdapter {
  val regex = "(^[^/]+)"
  val pattern = Pattern.compile(regex)
  val robot = Robot.robotService

  class ChatListener extends ChatMessageListener {

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
        robot.receive(HubotMessage(user, msg.getBody))
      }
    }
  }

  class ConnectListener(conn: XMPPTCPConnection)(onClose: () => Unit) extends ConnectionListener {
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
      conn.removeConnectionListener(this)
      conn.disconnect()
      onClose()
    }

    def connectionClosed =
     Logger.log("Error, connection closed","debug")

    def reconnectingIn(seconds: Int) =
     Logger.log("Reconnecting in " + seconds.toString + " seconds","debug")
  }

  class ChatMgrListener extends ChatManagerListener {
    def chatCreated(chat: Chat, createdLocally: Boolean) = {
      if (!createdLocally) {
        chat.addMessageListener(new ChatListener)
      }
    }
  }

}

class HipchatAdapter extends BaseAdapter {

  import HipchatAdapter._

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
    val chat = chatMgr.createChat(message.user.room, new ChatListener)
    chat.sendMessage(message.body)
  }

  val jid = getConfString("hipchat.jid","none")
  val password = getConfString("hipchat.password","none")

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
  val connectListener = new ConnectListener(conn)(run)
  chatMgr.addChatListener(new ChatMgrListener)


}
