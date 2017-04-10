package org.dberg.hubot

import org.dberg.hubot.brain.MapdbBackend
import org.scalatest.{ BeforeAndAfterAll, Suites }

class MasterTestSuite extends Suites(new HubotTestSuite, new BrainTestSuite) with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    MapdbBackend.deleteAll()
  }

  override def afterAll(): Unit = {
    MapdbBackend.shutdown()
  }
}
