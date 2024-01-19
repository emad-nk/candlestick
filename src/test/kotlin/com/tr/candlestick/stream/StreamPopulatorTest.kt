package com.tr.candlestick.stream

import com.tr.candlestick.service.InstrumentService
import com.tr.candlestick.service.QuoteService
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test

class StreamPopulatorTest {

    private val instrumentStream = mockk<InstrumentStream>(relaxed = true)
    private val quoteStream = mockk<QuoteStream>(relaxed = true)
    private val instrumentService = mockk<InstrumentService>(relaxed = true)
    private val quoteService = mockk<QuoteService>(relaxed = true)
    private val streamPopulator = StreamPopulator(
        instrumentStream = instrumentStream,
        quoteStream = quoteStream,
        instrumentService = instrumentService,
        quoteService = quoteService,
    )

    @Test
    fun `calls instrument stream and quote stream in order after startup`() {
        streamPopulator.startStreamsAfterStartup()

        verifyOrder {
            instrumentStream.connect(any())
            quoteStream.connect(any())
        }
    }
}
