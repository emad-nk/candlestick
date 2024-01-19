package com.tr.candlestick.job

import com.tr.candlestick.dummyInstrument
import com.tr.candlestick.service.CandlestickPopulator
import com.tr.candlestick.service.InstrumentService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class CandlestickJobTest {

    private val instrumentService = mockk<InstrumentService>(relaxed = true)
    private val candlestickPopulator = mockk<CandlestickPopulator>(relaxed = true)
    private val candlestickJob = CandlestickJob(
        instrumentService = instrumentService,
        candlestickPopulator = candlestickPopulator,
    )

    @Test
    fun `calls the respective services when the job runs`() {
        val instrument1 = dummyInstrument(isin = "1")
        val instrument2 = dummyInstrument(isin = "2")

        every { instrumentService.getScheduledInstruments() } returns listOf(instrument1, instrument2)

        candlestickJob.saveCandlesticks()

        verify(exactly = 1) { instrumentService.getScheduledInstruments() }
        verify(exactly = 1) { candlestickPopulator.saveCandlestick(instrument1) }
        verify(exactly = 1) { candlestickPopulator.saveCandlestick(instrument2) }
    }
}
