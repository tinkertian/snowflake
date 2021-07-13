package com.tinkertian.snowflake.server.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/system")
class SystemController {
    @Autowired
    lateinit var applicationContext: AbstractApplicationContext

    @PostMapping("/exit")
    fun exit(request: HttpServletRequest, response: HttpServletResponse) {
        logger.info("${request.remoteAddr} request exit...")
        if (request.remoteAddr == "127.0.0.1" || request.remoteAddr == "0:0:0:0:0:0:0:1") {
            SpringApplication.exit(applicationContext)
        } else {
            response.status = 404
        }
    }

    companion object {
        var logger: Logger = LoggerFactory.getLogger(SystemController::javaClass.name)
    }
}