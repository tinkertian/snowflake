package com.tinkertian.snowflake.spring

import com.tinkertian.snowflake.core.LargeSnowflake
import com.tinkertian.snowflake.core.SmallSnowflake
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.security.InvalidParameterException
import java.util.function.Function

@Component
open class SnowflakeProvider(context: ApplicationContext) {
  private val largePropertyName = "snowflake.node.large"
  private val smallPropertyName = "snowflake.node.small"
  private val largeNodeArray: Array<LargeSnowflake>
  private val largeEnd: Int
  private val smallNodeArray: Array<SmallSnowflake>
  private val smallEnd: Int

  private var largeIndex = 0
  private var smallIndex = 0

  init {
    val env = context.environment
    if (env.containsProperty(largePropertyName) && env.containsProperty(smallPropertyName)) {
      val large = env.getProperty(largePropertyName, Array<Int>::class.java)
      val small = env.getProperty(smallPropertyName, Array<Int>::class.java)
      if (large.isNullOrEmpty() || small.isNullOrEmpty()) throw InvalidParameterException("The snowflake.node.large and snowflake.node.small is empty. example: snowflake.node.large: 1023,1023")
      this.largeNodeArray = initSnowflake(large[0], large[1]) { node -> LargeSnowflake(node) }
      this.largeEnd = large[1] - large[0]
      this.smallNodeArray = initSnowflake(small[0], small[1]) { node -> SmallSnowflake(node) }
      this.smallEnd = small[1] - small[0]
    } else {
      throw InvalidParameterException("Not found snowflake.node.small and snowflake.node.small for environment, please checking you application.properties or application.yml")
    }
  }

  @Synchronized
  fun nextLarge(): Long {
    if (largeIndex > largeEnd) {
      largeIndex = 0
    }
    return largeNodeArray[largeIndex++].next()
  }

  @Synchronized
  fun nextSmall(): Long {
    if (smallIndex > smallEnd) {
      smallIndex = 0
    }
    return smallNodeArray[smallIndex++].next()
  }

  private inline fun <reified T : SmallSnowflake> initSnowflake(start: Int, end: Int, newT: Function<Int, T>): Array<T> {
    if (start > end) throw InvalidParameterException("LargeStart cannot be greater than largeEnd")
    val largeList = arrayListOf<T>()
    for (node in start..end) {
      largeList.add(newT.apply(node))
    }
    return largeList.toTypedArray()
  }
}