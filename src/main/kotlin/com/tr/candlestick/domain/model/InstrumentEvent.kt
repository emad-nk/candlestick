package com.tr.candlestick.domain.model

data class InstrumentEvent(
    val type: Type,
    val data: Instrument,
) {
    enum class Type {
        ADD,
        DELETE,
    }
}
