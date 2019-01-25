package com.tinkertian.snowflake.core

class LargeSnowflake(node: Int) : SmallSnowflake(node) {
    override fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}