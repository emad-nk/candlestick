package com.tr.candlestick.controller.dto

import com.tr.candlestick.domain.model.ISIN
import com.tr.candlestick.domain.model.Price
import java.time.Instant

data class CandlestickDTO(
    val isin: ISIN,
    val openTimestamp: Instant,
    val closeTimestamp: Instant,
    val openPrice: Price,
    val highPrice: Price,
    val lowPrice: Price,
    val closingPrice: Price,
)
