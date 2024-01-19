package com.tr.candlestick.domain.model

import com.tr.candlestick.common.nowAtMillis
import java.io.Serializable
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass

@IdClass(QuoteId::class)
@Entity(name = "Quote")
data class Quote(
    @Id
    val isin: ISIN,
    val price: Price,
    @Id
    val timestamp: Instant = nowAtMillis(),
) : Serializable

typealias Price = Double

data class QuoteId(
    var isin: ISIN? = null,
    var timestamp: Instant? = null,
) : Serializable
