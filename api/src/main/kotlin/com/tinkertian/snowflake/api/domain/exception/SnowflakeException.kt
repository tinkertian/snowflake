package com.tinkertian.snowflake.api.domain.exception

class SnowflakeException : RuntimeException {
    var code: Long = 0
    var id: Long = 0

    constructor() : super()
    constructor(code: Long) : super() {
        this.code = code
    }

    constructor(message: String?) : super(message)
    constructor(code: Long, message: String?) : super(message) {
        this.code = code
    }

    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(code: Long, message: String?, cause: Throwable?) : super(message, cause) {
        this.code = code
    }

    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(message, cause, enableSuppression, writableStackTrace)
}