package com.tinkertian.snowflake.client

import com.tinkertian.snowflake.api.domain.SnowflakeDomain
import jodd.http.HttpRequest

//import org.springframework.core.env.Environment

class SnowflakeClient {
    companion object {
        //var environment: Environment? = null

        fun next(): Long {
            var host = "localhost"
            return HttpRequest.get("$host:1010/sf/next-small")
                    .send()
                    .body()
                    .toLong()
        }

        fun resolver(sid: Long): SnowflakeDomain {
            return SnowflakeDomain.resolverLarge(sid)
        }
    }
}