package com.tinkertian.snowflake.api.domain

import java.sql.Timestamp
import java.time.LocalDateTime

open class SnowflakeDomain {
    var sid: Long = 0
    var node: Int = 0
    var seq: Int = 0
    var timestamp: Long = 0
    lateinit var date: LocalDateTime

    companion object {
        fun resolverLarge(sid: Long): SnowflakeDomain {
            var snowflake = SnowflakeDomain()
            snowflake.sid = sid
            snowflake.timestamp = Timestamp(sid.shr(22)).time
            snowflake.node = sid.or(-4194304).xor(-4194304).shr(12).toInt()
            snowflake.seq = sid.or(-4096).xor(-4096).toInt()
            snowflake.date = Timestamp(sid.shr(22)).toLocalDateTime()
            return snowflake
        }

        fun resolverSmall(sid: Long): SnowflakeDomain {
            var snowflake = SnowflakeDomain()
            snowflake.sid = sid
            snowflake.timestamp = Timestamp(sid.shr(22) * 1000).time
            snowflake.node = sid.or(-4194304).xor(-4194304).shr(12).toInt()
            snowflake.seq = sid.or(-4096).xor(-4096).toInt()
            snowflake.date = Timestamp(sid.shr(22) * 1000).toLocalDateTime()
            return snowflake
        }
    }
}