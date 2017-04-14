package org.dberg.hubot

import org.scalatest.DoNotDiscover
import scodec.codecs.ImplicitCodecs

@DoNotDiscover
class BrainTestSuite extends SpecBase with ImplicitCodecs {

  val brain = hubot.brainService

  "Hubot Brain" should "set a string correctly" in {
    brain.set[String]("testkey", "testvalue")
    val result = hubot.brainService.get[String]("testkey").getOrElse("failed")
    assert(result == "testvalue")
  }

  "Hubot Brain" should "set a List[String] correctly" in {
    brain.set[List[String]]("testkey", List("testvalue1", "testvalue2"))
    val result = brain.get[List[String]]("testkey").getOrElse(List())
    assert(result == List("testvalue1", "testvalue2"))
  }

  "Hubot Brain" should "set an Int correctly" in {
    brain.set[Int]("intkey", 1000)
    val result = brain.get[Int]("intkey").getOrElse(0)
    assert(result == 1000)
  }

}