package com.tr.candlestick.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("instrument")
@ConstructorBinding
data class InstrumentProperties(
    val maxPageSize: Int,
    val secondsToAdd: Int,
)
