package org.dberg.hubot.brain

import scodec.bits.BitVector
import scodec.{ Codec ⇒ SCodec, _ }
import scodec.bits._

trait BrainBackendBase {
  def setKey[A: SCodec](key: String, value: A): Unit
  def getKey[A: SCodec](key: String): A
  def shutdown(): Unit

  def decode[A: SCodec](value: Array[Byte]): A = {
    val data = BitVector(value)
    SCodec.decode[A](data).require.value
  }

  def encode[A: SCodec](value: A): Array[Byte] = {
    val data = SCodec.encode(value).require
    data.toByteArray
  }

}