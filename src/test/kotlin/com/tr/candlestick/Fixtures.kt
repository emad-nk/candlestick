package com.tr.candlestick

import com.tr.candlestick.common.beginningOfCurrentMinute
import com.tr.candlestick.common.beginningOfLastMinute
import com.tr.candlestick.common.nowAtMillis
import com.tr.candlestick.domain.model.Candlestick
import com.tr.candlestick.domain.model.ISIN
import com.tr.candlestick.domain.model.Instrument
import com.tr.candlestick.domain.model.Quote
import java.time.Instant
import java.util.UUID.randomUUID

fun dummyInstrument(
    isin: ISIN = randomUUID().toString(),
    description: String = "some description",
    scheduledTo: Instant = nowAtMillis(),
) = Instrument(
    isin = isin,
    description = description,
    scheduledTo = scheduledTo,
)

fun dummyQuote(
    isin: ISIN = randomUUID().toString(),
    price: Double = 1.5,
    timestamp: Instant = nowAtMillis(),
) = Quote(
    isin = isin,
    price = price,
    timestamp = timestamp,
)

fun dummyCandlestick(
    isin: ISIN = randomUUID().toString(),
    openTimestamp : Instant= nowAtMillis().beginningOfLastMinute(),
    closeTimestamp : Instant = nowAtMillis().beginningOfCurrentMinute().minusSeconds(1),
    openPrice: Double = 1.1,
    highPrice: Double = 4.0,
    lowPrice: Double = 0.6,
    closingPrice: Double = 1.7
) = Candlestick(
    isin = isin,
    openTimestamp = openTimestamp,
    closeTimestamp = closeTimestamp,
    openPrice = openPrice,
    highPrice = highPrice,
    lowPrice = lowPrice,
    closingPrice = closingPrice
)
