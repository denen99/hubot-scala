package org.dberg.hubot.adapter

import java.util.regex.Pattern
import javax.net.ssl.{ HostnameVerifier, SSLSession }
import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.Hubot
import collection.JavaConverters._
import org.jivesoftware.smack._
import org.jivesoftware.smack.chat.{ Chat, ChatManager, ChatManagerListener, ChatMessageListener }
import org.jivesoftware.smackx.muc.{ DiscussionHistory, MultiUserChat, MultiUserChatManager, RoomInfo }
import org.jivesoftware.smack.tcp.{ XMPPTCPConnection, XMPPTCPConnectionConfiguration }
import org.dberg.hubot.models.{ MessageType, User, Message => HubotMessage }
import org.dberg.hubot.utils.Helpers._
import org.jivesoftware.smack.packet.{ Presence, Stanza, Message => SmackMessage }

class HipchatAdapter(hubot: Hubot) extends BaseAdapter(hubot: Hubot) with StrictLogging {

  object HipchatAdapterTools {
    val regex = "(^[^/]+)/?(.*)?"
    val pattern = Pattern.compile(regex)

    def getJid(from: String): String = {
      val matcher = pattern.matcher(from)
      if (matcher.find())
        matcher.group(1)
      else
        "Invalid JID"
    }

    def getPresence(from: String): String = {
      val matcher = pattern.matcher(from)
      if (matcher.find())
        matcher.group(2)
      else
        "Invalid Presence"
    }

    /**
     * ****************************************
     * Callback for 1-1 Chat messages
     * *****************************************
     */
    class ChatListener extends ChatMessageListener with StrictLogging {

      def processMessage(chat: Chat, msg: SmackMessage) = {
        logger.debug("chat: " + chat.toString)
        logger.debug("msg: " + msg.toString)
        if (msg.getBody != null) {
          val jid = getJid(msg.getFrom)
          val user = User(jid)
          hubot.robotService.receive(HubotMessage(user, msg.getBody, MessageType.DirectMessage))
        }
      }
    }

    /**
     * ****************************************
     * Callback for Connection Events
     * ******************************************
     */
    class ConnectListener(conn: XMPPTCPConnection)(onClose: () => Unit) extends ConnectionListener {
      def connected(connection: XMPPConnection) =
        logger.debug("Received connection from user : " + connection.getUser)

      def reconnectionFailed(e: Exception) =
        logger.error("Connection failed : ", e)

      def reconnectionSuccessful =
        logger.debug("Reconnection successful")

      def authenticated(connection: XMPPConnection, resumed: Boolean) =
        logger.info("Authenticated Successful")

      def connectionClosedOnError(e: Exception) = {
        logger.info("Connection closed with error, attempting to re-connect", e)
        conn.removeConnectionListener(this)
        conn.disconnect()
        onClose()
      }

      def connectionClosed =
        logger.debug("Error, connection closed")

      def reconnectingIn(seconds: Int) =
        logger.debug("Reconnecting in " + seconds.toString + " seconds")
    }

    /**
     * ****************************************
     * Callback for 1-1 Chat messages
     * *****************************************
     */
    class ChatMgrListener extends ChatManagerListener {
      def chatCreated(chat: Chat, createdLocally: Boolean) = {
        if (!createdLocally) {
          chat.addMessageListener(new ChatListener)
        }
      }
    }

    /**
     * ****************************************
     * Callback for GroupChats
     * *****************************************
     */
    class MessageMgrListener extends MessageListener with StrictLogging {

      def processMessage(message: SmackMessage) = {
        logger.debug("received muc message " + message)
        if (message.getBody != null) {
          val jid = getJid(message.getFrom)
          val user = User(jid)
          //Dont process messages if the bot sent them to avoid loops !
          if (getPresence(message.getFrom) != chatAlias)
            hubot.robotService.receive(HubotMessage(user, message.getBody, MessageType.GroupMessage))
        }
      }

    }

  }

  import HipchatAdapterTools._

  def run() = {
    conn.addConnectionListener(connectListener)
    if (!conn.isConnected)
      conn.connect()
    if (!conn.isAuthenticated)
      conn.login()
    if (conn.isAuthenticated) {

      mucMgr.getHostedRooms("conf.hipchat.com").asScala.foreach { room =>
        val muc = mucMgr.getMultiUserChat(room.getJid)
        muc.addMessageListener(new MessageMgrListener)
        val history = new DiscussionHistory()
        history.setMaxStanzas(0)
        logger.debug("Joining MUC room " + muc.getRoom() + " : " + muc.getNickname)
        try {
          muc.join(chatAlias, null, history, SmackConfiguration.getDefaultPacketReplyTimeout)
        } catch {
          case e: Exception =>
            logger.error("Unable to join MUC room " + muc.getRoom + ": ", e)
        }
      }
      while (conn.isAuthenticated) {
        // do nothing
      }
      logger.error("Error - disconnected from server, reconnecting")
      run()
    }
  }

  def send(message: HubotMessage) = {
    logger.debug("Hipchat Adapter sending message : " + message.toString)
    message.messageType match {
      case MessageType.DirectMessage =>
        val chat = chatMgr.createChat(message.user.room, new ChatListener)
        chat.sendMessage(message.body)
      case MessageType.GroupMessage =>
        logger.debug("Sending message back to MUC " + message.user.room)
        val muc = mucMgr.getMultiUserChat(message.user.room)
        muc.sendMessage(message.body)
    }

  }

  val jid = getConfString("hipchat.jid", "none")
  val password = getConfString("hipchat.password", "none")
  val chatAlias = getConfString("hipchat.chatAlias", "ScalaBot")

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
  chatMgr.addChatListener(new ChatMgrListener)

  val mucMgr = MultiUserChatManager.getInstanceFor(conn)

  val connectListener = new ConnectListener(conn)(run)

}
