package com.tinkertian.snowflake.core

open class SmallSnowflake(private var node: Int) {
    private val nodeShl = 10
    private val sequenceShl = 12
    private val maxNode: Short = 1023
    private val maxSequence: Short = 4095
    private var sequence: Short = 0
    private var referenceTime: Long = 0

    init {
        if (node < 0 || node > maxNode) {
            throw IllegalArgumentException(String.format("node must be between %s and %s", 0, maxNode))
        }
    }

    @Synchronized
    operator fun next(): Long {
        val currentTime = this.getCurrentTimeMillis()
        if (currentTime < referenceTime) {
            throw RuntimeException(String.format("Last referenceTime %s is after reference time %s", referenceTime, currentTime))
        } else if (currentTime > referenceTime) {
            this.sequence = 0
        } else {
            if (this.sequence < maxSequence) {
                this.sequence++
            } else {
                throw RuntimeException("Sequence exhausted at " + this.sequence)
            }
        }
        referenceTime = currentTime

        return currentTime.shl(nodeShl).shl(sequenceShl)
                .or(node.shl(sequenceShl).toLong())
                .or(this.sequence.toLong())
    }

    open fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis() / 1000
    }
}