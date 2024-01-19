package com.tr.candlestick.service

import com.tr.candlestick.common.beginningOfCurrentMinute
import com.tr.candlestick.common.nowAtMillis
import com.tr.candlestick.domain.model.Instrument
import com.tr.candlestick.domain.repository.InstrumentRepository
import com.tr.candlestick.property.InstrumentProperties
import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@Service
@EnableConfigurationProperties(InstrumentProperties::class)
class InstrumentService(
    private val instrumentRepository: InstrumentRepository,
    private val instrumentProperties: InstrumentProperties,
) {
    fun getScheduledInstruments(): List<Instrument> {
        return instrumentRepository.findScheduledInstruments(
            upToTime = nowAtMillis().beginningOfCurrentMinute(),
            intervalToAddInSeconds = instrumentProperties.secondsToAdd,
            limit = instrumentProperties.maxPageSize,
        )
    }

    fun save(instrument: Instrument) = instrumentRepository.save(instrument)

    fun delete(instrument: Instrument) {
        if (existsByIsin(instrument.isin)) {
            instrumentRepository.deleteByIsin(instrument.isin)
        } else {
            logger.warn { "Instrument with ISIN ${instrument.isin} not found" }
        }
    }

    fun existsByIsin(isin: String): Boolean = instrumentRepository.existsByIsin(isin)

    companion object {
        val logger = KotlinLogging.logger { }
    }
}
