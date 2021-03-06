#Hubot Scala

This is a (poor?) attempt at trying to write a simplified version of Hubot in Scala.  It allows you to extend it by writing your own listener classes and middleware classes.  Quite frankly, I found the coffeescript Hubot code a little nasty to follow and without static typing , it became a real chore trying to piece it together.

### How it works 

The current implementation is fairly simple (explained in more detail below).  There are 4 main components, Adapters, Listeners, Brain and Middleware.

### Adapters 
Adapters (of which there can be 1), extends BaseAdapter and implements a few basic methods that is used to tie to your chat source.  So there could be an adapter for XMPP, Campfire, Slack, etc.  For now its simply, send() [Send a message], receive() [receive a message] and run() [Run the adapter].  Set the property hubot.adapter in application.conf using the full className.

### Middleware 
Middleware, are simply an intermediary you can write that every message goes through.  Each piece of middleware should return either a Left(MiddlewareFailure) if the message should be blocked or a Right(MiddlewareSuccess) if the message should be passed through.  On the first Left() the chain is halted.  Useful for things like rate limiting, blocking certain users, etc.  You get full access to the brain, etc for persistent data if needed.

### Listeners
Listeners, can be one of two types Respond or Hear.  Hear, listens to every message in the chat, while Respond reacts when the bot is addressed by its name (configurable in appplication.conf).  A Listener is a simple class you write that defines a callback method (which returns a PartialFunction[Message,Unit]) that you need to implement.  Each message is passed through this callback and the callback method can match against anything available (sender, message body, etc).  Additionally, any groupings you used in your regex are passed back as a groups Seq[String] so you can obtain access to those references.

### Brain
Brain, is a configurable backend that allows you to have persistence.  Currently, hubot-scala ships with MapDB as a default brain backend, but you are free to write your own.  Simply extend BrainBackendBase and implement 3 methods, getKey, setKey and shutdown().  

Also note, that for serialization purposes, mostly to allow the storing of complex types as values (like List() and Map()), Scodec is used as the serialization.  The gets are also wrapped in a Try{} since a NullPointer is returned when trying to deserialize to an invalid type.  This means the syntax for getting a value from the brain would be something like:

```scala
brain.get[A](someKey).getOrElse(defaultValue)
```
	
where A is the type you are deserializing to (String, List[Int]), someKey is the key to your hash and defaultValue is the value to return if the key is not found.  Additionally to store a value, the format would be 

```scala
brain.set[A](someKey, someValue)
```

Again, someValue needs to be of type A, and someKey is a String.

### Getting started 

To get started, first, you need to put the hubot-scala library in your build.sbt as follows

```scala
libraryDependencies += "org.dberg" % "hubot-scala_2.11" % "${latest}"
```
        
        
Once you do that, in your new project create whatever classes you need.  For example, here is a simple listener, that responds to hello when the Bot is addressed

```scala
class TestListener(hubot: HubotBase) extends Listener(hubot, ListenerType.Respond) {
  val callback: Callback = {
  case r"hello\s+world" =>
    Logger.log("Running callback for listner TestListener","debug")
    robot.send(Message(message.user,"Hubot says hello!"", message.messageType))
  }
  val helpString = Some("hello world -> Allows hubot to say hello !")
}
```

The structure of the Message class is Message(user: User, body: String, messageType: MessageTypeValue, params: Map[String,String]).  The params allows the adapter a place to stick additional information that then gets passed through to the listener and middleware.

To create a middleware is similar to the listener.  Create a new class that extends Middleware and do whatever you want with the message.  Then simply define a method named "execute(message: Message)" and you are good to go.  You can keep state, call an external service, etc.  Here is an example of middleware that checks if the message matches the word "blacklist".

```scala
class TestMiddleware(hubot: HubotBase) extends Middleware(hubot) {

 def execute(message: Message) = {
  if (message.body == "blacklist") {
   Left(MiddlewareError("Sorry this is a blacklist"))
  }
  else {
   Right(MiddlewareSuccess())
  }
 }
 
}
```

### Configuration

2 configuration files should be used and placed in src/main/resources.  The first is application.conf (check the sample in this repo). The format is like so 

    hubot {
      name = "yourbotname"

      listeners = ["org.yourdomain.listeners.YourFirstListener",   "org.yourdomain.listeners.YourOtherListener"]

      middleware = []

      adapter = "org.dberg.hubot.adapter.ShellAdapter"
}    

The second is the logback.xml file.  Tweak this file depending on how you want your logging to work.

    <configuration debug="true">

    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
      <file>logs/hubot.log</file>

      <encoder>
        <pattern>%date %level [%file:%line] %msg%n</pattern>
      </encoder>
    </appender>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
     <encoder>
      <pattern>%date %level [%file:%line] %msg%n</pattern>
     </encoder>
    </appender>

     <root level="DEBUG">
      <appender-ref ref="FILE" />
      <appender-ref ref="STDOUT" />
     </root>
    </configuration>


### Running the Bot 

 Once you have everything configured you simply need to start the bot.  Create a simple Main class as so 
 
```scala
import org.dberg.hubot.HubotRunner

object Main {
  def main(args: Array[String]) = {
    HubotRunner.start
  }
}
```

