package com.tr.candlestick.service

import com.tr.candlestick.controller.dto.CandlestickDTO

interface CandlestickManager {
    fun getCandlesticks(isin: String): List<CandlestickDTO>
}
