package org.dberg.hubot.adapter

import java.util.regex.Pattern
import javax.net.ssl.{ HostnameVerifier, SSLSession }

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.Hubot

import collection.JavaConverters._
import org.jivesoftware.smack._
import org.jivesoftware.smack.chat.{ Chat, ChatManager, ChatManagerListener, ChatMessageListener }
import org.jivesoftware.smackx.muc._
import org.jivesoftware.smack.tcp.{ XMPPTCPConnection, XMPPTCPConnectionConfiguration }
import org.dberg.hubot.models.{ MessageType, User, Message => HubotMessage }
import org.dberg.hubot.utils.Helpers._
import org.jivesoftware.smack.packet.{ Presence, Stanza, Message => SmackMessage }
import org.jivesoftware.smackx.ping.{ PingFailedListener, PingManager }

import scala.util.control.NonFatal

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
        logger.debug("Received message " + msg.toString)
        if (msg.getBody != null) {
          val jid = getJid(msg.getFrom)
          val user = User(jid)
          robot.receive(HubotMessage(user, msg.getBody, MessageType.DirectMessage))
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
        logger.info("Received connection from user : " + connection.getUser)

      def reconnectionFailed(e: Exception) =
        logger.error("Connection failed : ", e)

      def reconnectionSuccessful =
        logger.info("Reconnection successful")

      def authenticated(connection: XMPPConnection, resumed: Boolean) =
        logger.info("Authenticated Successful")

      def connectionClosedOnError(e: Exception) = {
        logger.error("Connection closed with error, attempting to re-connect", e)
        conn.removeConnectionListener(this)
        mucMgr.removeInvitationListener(mucListener)
        conn.disconnect()
        onClose()
      }

      def connectionClosed = {
        logger.error("Error, connection closed")
        conn.removeConnectionListener(this)
        mucMgr.removeInvitationListener(mucListener)
        conn.disconnect()
        onClose()
      }

      def reconnectingIn(seconds: Int) =
        logger.info("Reconnecting in " + seconds.toString + " seconds")
    }

    /**
     * ***************************
     * Callback for Ping Failures
     * ********************************
     */
    class PingMgrListener extends PingFailedListener {
      def pingFailed() =
        logger.error("XMPP Ping Failed")
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
          //TODO:  This should be made generic for any adapter or future adapters are screwed !
          if (getPresence(message.getFrom) != chatAlias)
            robot.receive(HubotMessage(user, message.getBody, MessageType.GroupMessage))
        }
      }

    }

    /**
     * ****************************************
     * Callback for GroupChat Invites
     * *****************************************
     */
    class MucInvitationListener extends InvitationListener {
      override def invitationReceived(conn: XMPPConnection, room: MultiUserChat, inviter: String, reason: String, password: String, message: SmackMessage) = {
        logger.debug("Received invite to room " + room.getRoom)
        val history = new DiscussionHistory()
        history.setMaxStanzas(0)
        val muc = mucMgr.getMultiUserChat(room.getRoom)
        muc.addMessageListener(new MessageMgrListener)
        try {
          muc.join(chatAlias, null, history, SmackConfiguration.getDefaultPacketReplyTimeout)
        } catch {
          case e: Exception =>
            logger.error("Unable to join via invite, MUC room " + muc.getRoom + ": ", e)
        }
      }
    }

  }

  import HipchatAdapterTools._

  private def leaveJoinedRooms() = {
    mucMgr.getJoinedRooms.asScala.foreach { room =>
      logger.info("Forcing leave of room " + room)
      val m = mucMgr.getMultiUserChat(room)
      try {
        m.leave()
      } catch { case NonFatal(e) => logger.error("Error leaving room, " + e.getMessage) }
    }
  }

  def run() = {
    logger.info("Starting HipChat Run loop")

    conn.addConnectionListener(connectListener)
    mucMgr.addInvitationListener(mucListener)

    if (!conn.isConnected) {
      logger.info("XMPP connection is connected")
      conn.connect()
    }
    if (!conn.isAuthenticated) {
      logger.info("XMPP connection is not authenticated, logging in")
      conn.login()
    }
    if (conn.isAuthenticated) {

      pingMgr.pingMyServer(true) // Just a test

      leaveJoinedRooms() // First leave the rooms

      mucMgr.getHostedRooms("conf.hipchat.com").asScala.foreach { room =>
        val muc = mucMgr.getMultiUserChat(room.getJid)
        if (muc.isJoined) {
          logger.info("Skipping joined room " + muc.getRoom)
        } else {
          val history = new DiscussionHistory()
          history.setMaxStanzas(0)
          logger.info("Joining MUC room " + muc.getRoom() + " : " + muc.getNickname)
          try {
            muc.join(chatAlias, null, history, SmackConfiguration.getDefaultPacketReplyTimeout)
            muc.addMessageListener(new MessageMgrListener)
          } catch {
            case e: Exception =>
              logger.error("Unable to join MUC room " + muc.getRoom + ": ", e)
          }
        }
      }

      try {
        while (conn.isAuthenticated) {
          // do nothing
          Thread.sleep(5000)
        }
      } catch {
        case NonFatal(e) =>
          logger.error("Hipchat RunLoop failed, restarting: " + e.getMessage)
          run()
      }

      //TODO: review this
      // lets rely on listeners for now
      //      conn.disconnect()
      //      logger.error("Error - disconnected from server, reconnecting")
      //      run()
    } else { logger.error("XMPP Connection is not authenticated") }

    logger.error("We exited the HipChat Adapter run loop....")
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
    .setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible)
    .setConnectTimeout(60000)

  val conn = new XMPPTCPConnection(conf.build())

  conn.setPacketReplyTimeout(60000)

  val reconnectMgr = ReconnectionManager.getInstanceFor(conn)
  reconnectMgr.enableAutomaticReconnection()
  reconnectMgr.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY)

  val pingMgr = PingManager.getInstanceFor(conn)
  pingMgr.setPingInterval(10)
  pingMgr.registerPingFailedListener(new PingMgrListener)

  val chatMgr = ChatManager.getInstanceFor(conn)
  chatMgr.addChatListener(new ChatMgrListener)

  val mucMgr = MultiUserChatManager.getInstanceFor(conn)
  val mucListener = new MucInvitationListener

  val connectListener = new ConnectListener(conn)(run)

}
