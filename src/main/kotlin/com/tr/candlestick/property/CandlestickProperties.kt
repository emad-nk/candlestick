package com.tr.candlestick.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("candlestick")
@ConstructorBinding
data class CandlestickProperties(
    val maxPageSize: Int,
)
