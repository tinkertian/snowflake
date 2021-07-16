package com.tinkertian.snowflake.server.controller

import com.tinkertian.snowflake.api.domain.SnowflakeDomain
import com.tinkertian.snowflake.spring.SnowflakeProvider
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.nio.ByteBuffer
import java.time.format.DateTimeFormatter

@RestController
open class SnowflakeController(private val provider: SnowflakeProvider) {
  private var logger = LoggerFactory.getLogger(SnowflakeController::class.java)

  @GetMapping
  fun welcome(): Array<String> {
    return arrayOf(
        "/next",
        "/next-small",
        "/next-small-batch/{count}",
        "/next-small-bin/{count}",
        "/next-small-info",
        "/next-large",
        "/next-large-batch/{count}",
        "/next-large-bin/{count}",
        "/next-large-info",
    )
  }

  @GetMapping("/next")
  fun next(): String {
    return nextSmall()
  }

  @GetMapping("/next-small")
  fun nextSmall(): String {
    return provider.nextSmall().toString()
  }

  @GetMapping("/next-small-bin/{count}")
  fun nextSmallBin(@PathVariable("count") count: Int): ResponseEntity<ByteArray> {
    val buffer: ByteBuffer = ByteBuffer.allocate(count * 8)
    val startNano = System.nanoTime()
    for (i in 1..count) {
      buffer.putLong(provider.nextSmall())
    }
    val endNano = System.nanoTime()
    logger.info("Time to batch generate snowflake ：{} nm, {} ms", endNano - startNano, (endNano - startNano) / 1000 / 1000)
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(buffer.array())
  }

  @GetMapping("/next-small-batch/{count}")
  fun nextSmallBatch(@PathVariable("count") count: Int): Array<String> {
    return Array(count) { provider.nextSmall().toString() }
  }

  @GetMapping("/next-large")
  fun nextLarge(): String {
    return provider.nextLarge().toString()
  }

  @GetMapping("/next-small-info")
  fun nextSmallInfo(): SnowflakeDomain {
    val sf = SnowflakeDomain.resolverSmall(provider.nextSmall())
    this.log(sf)
    return sf
  }

  @GetMapping("/next-large-info")
  fun nextLargeInfo(): SnowflakeDomain {
    val sf = SnowflakeDomain.resolverLarge(provider.nextLarge())
    this.log(sf)
    return sf
  }

  @GetMapping("/next-large-bin/{count}")
  fun nextLargeBin(@PathVariable("count") count: Int): ResponseEntity<ByteArray> {
    val buffer: ByteBuffer = ByteBuffer.allocate(count * 8)
    for (i in 1..count) {
      buffer.putLong(provider.nextLarge())
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