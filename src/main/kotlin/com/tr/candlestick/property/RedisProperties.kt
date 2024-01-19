package com.tr.candlestick.property

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("redis")
data class RedisProperties(
    val ttl: Map<String, Duration>,
)
