package com.tr.candlestick.domain.model

import com.tr.candlestick.common.nowAtMillis
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "Instrument")
data class Instrument(
    @Id
    val isin: ISIN,
    val description: String,
    val scheduledTo: Instant = nowAtMillis(),
)
typealias ISIN = String
