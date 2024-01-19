package com.tr.candlestick.domain.model

import com.tr.candlestick.controller.dto.CandlestickDTO
import java.io.Serializable
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass

@IdClass(CandlestickId::class)
@Entity(name = "candlestick")
data class Candlestick(
    @Id
    val isin: ISIN,
    @Id
    val openTimestamp: Instant,
    val closeTimestamp: Instant,
    val openPrice: Price,
    val highPrice: Price,
    val lowPrice: Price,
    val closingPrice: Price,
) : Serializable

data class CandlestickId(
    var isin: ISIN? = null,
    var openTimestamp: Instant? = null,
) : Serializable

fun Candlestick.toDTO() =
    CandlestickDTO(
        isin = isin,
        openTimestamp = openTimestamp,
        closeTimestamp = closeTimestamp,
        openPrice = openPrice,
        highPrice = highPrice,
        lowPrice = lowPrice,
        closingPrice = closingPrice,
    )
