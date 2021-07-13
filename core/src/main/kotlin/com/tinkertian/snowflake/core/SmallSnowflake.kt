package com.tinkertian.snowflake.core

import com.tinkertian.snowflake.api.domain.exception.SnowflakeException
import org.slf4j.LoggerFactory

open class SmallSnowflake(node: Int) {
  private var logger = LoggerFactory.getLogger(SmallSnowflake::class.java)
  private val nodeShl = 10
  private val sequenceShl = 12
  private val nodeAndSequenceShl = nodeShl + sequenceShl
  private val maxNode: Short = 1023
  private val maxSequence: Short = 4095
  private var sequence: Long = 0
  private var referenceTime: Long = 0
  private val nodeLong = node.toLong()

  init {
    if (node < 0 || node > maxNode) {
      throw IllegalArgumentException(String.format("node must be between %s and %s", 0, maxNode))
    }
  }

  @Synchronized
  operator fun next(): Long {
    val currentTime = this.getCurrentTimeMillis()
    if (currentTime < referenceTime) {
      throw RuntimeException("Last referenceTime $referenceTime is after reference time $currentTime")
    } else if (currentTime > referenceTime) {
      this.sequence = 0
    } else {
      if (this.sequence < maxSequence) {
        this.sequence++
      } else {
        throw SnowflakeException("Sequence exhausted at ${this.sequence}, timestamp=${currentTime}")
      }
    }
    referenceTime = currentTime

    val startNano = System.nanoTime()
    val snowflakeId = currentTime.shl(nodeAndSequenceShl)
        .or(nodeLong.shl(sequenceShl))
        .or(this.sequence)
    val endNano = System.nanoTime()
    logger.debug("It takes time to generate a single snowflake：{} nm", endNano - startNano)

    return snowflakeId
  }

  open fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis() / 1000
  }
}