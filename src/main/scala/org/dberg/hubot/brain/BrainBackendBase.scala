package org.dberg.hubot.brain

import scodec.bits.BitVector
import scodec.{ Codec => SCodec, _ }
import scodec.bits._

import scala.util.Try

trait BrainBackendBase {
  def setKey[A: SCodec](key: String, value: A): Unit
  def getKey[A: SCodec](key: String): Try[A]
  def shutdown(): Unit

  def decode[A: SCodec](value: Array[Byte]): Try[A] = Try {
    val data = BitVector(value)
    SCodec.decode[A](data).require.value
  }

  def encode[A: SCodec](value: A) = Try {
    val data = SCodec.encode(value).require
    data.toByteArray
  }

}