package com.tr.candlestick.service

import com.tr.candlestick.domain.model.Instrument

interface CandlestickPopulator {
    fun saveCandlestick(instrument: Instrument)
}
