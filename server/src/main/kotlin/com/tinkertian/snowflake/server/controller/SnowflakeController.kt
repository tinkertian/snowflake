package com.tinkertian.snowflake.server.controller

import com.tinkertian.snowflake.api.domain.SnowflakeDomain
import com.tinkertian.snowflake.spring.SnowflakeComponent
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.ByteBuffer
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/sf")
open class SnowflakeController {
  private var logger = LoggerFactory.getLogger(SnowflakeController::class.java)

  @GetMapping("/next")
  fun next(): String {
    return nextSmall()
  }

  @GetMapping("/next-small")
  fun nextSmall(): String {
    return SnowflakeComponent.nextSmall().toString()
  }

  @GetMapping("/next-small-bin/{count}")
  fun nextSmallBin(@PathVariable("count") count: Int): ResponseEntity<ByteArray> {
    val buffer: ByteBuffer = ByteBuffer.allocate(count * 8)
    for (i in 1..count) {
      buffer.putLong(SnowflakeComponent.nextSmall())
    }
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(buffer.array())
  }

  @GetMapping("/next-small-batch/{count}")
  fun nextSmallBatch(@PathVariable("count") count: Int): Array<String> {
    return Array(count) { SnowflakeComponent.nextSmall().toString() }
  }

  @GetMapping("/next-large")
  fun nextLarge(): String {
    return SnowflakeComponent.nextLarge().toString()
  }

  @GetMapping("/next-small-info")
  fun nextSmallInfo(): SnowflakeDomain {
    val sf = SnowflakeDomain.resolverSmall(SnowflakeComponent.nextSmall())
    this.log(sf)
    return sf
  }

  @GetMapping("/next-large-info")
  fun nextLargeInfo(): SnowflakeDomain {
    val sf = SnowflakeDomain.resolverLarge(SnowflakeComponent.nextLarge())
    this.log(sf)
    return sf
  }

  @GetMapping("/next-large-bin/{count}")
  fun nextLargeBin(@PathVariable("count") count: Int): ResponseEntity<ByteArray> {
    val buffer: ByteBuffer = ByteBuffer.allocate(count * 8)
    for (i in 1..count) {
      buffer.putLong(SnowflakeComponent.nextLarge())
    }
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(buffer.array())
  }

  @GetMapping("/resolver/{sid}")
  fun resolver(@PathVariable("sid") sid: Long): SnowflakeDomain {
    val sf = SnowflakeDomain.resolverSmall(sid)
    this.log(sf)
    return sf
  }

  fun log(sf: SnowflakeDomain) {
    logger.debug("sid={}, dateTime={}, timestamp={}, node={}, sn={}", sf.sid, sf.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), sf.timestamp, sf.node, sf.seq)
  }
}