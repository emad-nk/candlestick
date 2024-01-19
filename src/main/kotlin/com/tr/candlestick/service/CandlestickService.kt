package com.tr.candlestick.service

import com.tr.candlestick.common.beginningOfCurrentMinute
import com.tr.candlestick.common.beginningOfLastMinute
import com.tr.candlestick.common.beginningOfNextMinute
import com.tr.candlestick.common.nowAtMillis
import com.tr.candlestick.controller.dto.CandlestickDTO
import com.tr.candlestick.domain.model.Candlestick
import com.tr.candlestick.domain.model.Instrument
import com.tr.candlestick.domain.model.toDTO
import com.tr.candlestick.domain.repository.CandlestickRepository
import com.tr.candlestick.exception.IsinNotFoundException
import com.tr.candlestick.property.CandlestickProperties
import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@Service
@EnableConfigurationProperties(CandlestickProperties::class)
class CandlestickService(
    private val quoteService: QuoteService,
    private val candlestickRepository: CandlestickRepository,
    private val candlestickProperties: CandlestickProperties,
    private val instrumentService: InstrumentService,
) : CandlestickManager, CandlestickPopulator {

    override fun getCandlesticks(isin: String): List<CandlestickDTO> {
        if (instrumentService.existsByIsin(isin).not()) {
            throw IsinNotFoundException("Isin $isin not found")
        }
        return candlestickRepository.findByIsin(
            isin = isin,
            limit = candlestickProperties.maxPageSize,
        ).map { it.toDTO() }
    }

    override fun saveCandlestick(instrument: Instrument) {
        val now = nowAtMillis()
        val quotes = quoteService.findQuotesByIsinWithinTimeFrame(
            isin = instrument.isin,
            from = now.beginningOfLastMinute(),
            to = now.beginningOfCurrentMinute(),
        )
        logger.info { "Found ${quotes.size} quotes for isin ${instrument.isin}, creating a candlestick out of them if not 0" }

        if (quotes.isNotEmpty()) {
            candlestickRepository.save(
                Candlestick(
                    isin = instrument.isin,
                    openTimestamp = quotes.first().timestamp.beginningOfCurrentMinute(),
                    closeTimestamp = quotes.last().timestamp.beginningOfNextMinute(),
                    openPrice = quotes.first().price,
                    highPrice = quotes.maxOf { it.price },
                    lowPrice = quotes.minOf { it.price },
                    closingPrice = quotes.last().price,
                ),
            )
        }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
