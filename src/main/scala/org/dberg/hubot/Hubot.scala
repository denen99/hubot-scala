package org.dberg.hubot

import com.typesafe.config.{ConfigFactory, Config}
import com.typesafe.scalalogging.{StrictLogging, LazyLogging}
import org.dberg.hubot.adapter.{TestAdapter, BaseAdapter}
import org.dberg.hubot.models._
import org.dberg.hubot.middleware._


object Hubot extends StrictLogging {

  val config = ConfigFactory.load()
  val adapter = "org.dberg.hubot.adapter.TestAdapter" // config.getString("adapter.class")
  //val adapterClass = Class.forName(adapter).newInstance
  val listeners = Seq(TestListener(".*"), TestListener2())
  val middleware = Seq(new TestMiddleware())

  val robot = new Robot(new TestAdapter(),
                        listeners,
                        middleware)


  def main(args: Array[String]): Unit = {
    robot.run()
  }

}
