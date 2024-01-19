package com.tr.candlestick.job

import com.tr.candlestick.service.CandlestickPopulator
import com.tr.candlestick.service.InstrumentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Runs beginning of every minute to populate candlesticks
 */

@Component
class CandlestickJob(
    private val instrumentService: InstrumentService,
    private val candlestickPopulator: CandlestickPopulator,
) {

    @Scheduled(cron = "\${job.cron.generate-candlestick.scheduled}")
    fun saveCandlesticks() {
        logger.info { "Job started to save candlesticks" }
        instrumentService
            .getScheduledInstruments()
            .also { logger.info { "found ${it.size} instruments to save candlesticks" } }
            .forEach {
                CoroutineScope(Dispatchers.IO).launch {
                    candlestickPopulator.saveCandlestick(it)
                }
            }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
