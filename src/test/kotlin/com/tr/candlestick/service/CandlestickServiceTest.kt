package com.tr.candlestick.service

import com.tr.candlestick.common.beginningOfCurrentMinute
import com.tr.candlestick.common.beginningOfLastMinute
import com.tr.candlestick.common.beginningOfNextMinute
import com.tr.candlestick.domain.model.Candlestick
import com.tr.candlestick.domain.model.toDTO
import com.tr.candlestick.domain.repository.CandlestickRepository
import com.tr.candlestick.dummyCandlestick
import com.tr.candlestick.dummyInstrument
import com.tr.candlestick.dummyQuote
import com.tr.candlestick.exception.IsinNotFoundException
import com.tr.candlestick.property.CandlestickProperties
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZoneOffset.UTC
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CandlestickServiceTest {

    private val candlestickProperties = CandlestickProperties(maxPageSize = 30)
    private val quoteService = mockk<QuoteService>()
    private val candlestickRepository = mockk<CandlestickRepository>(relaxed = true)
    private val instrumentService = mockk<InstrumentService>()

    private val candlestickService = CandlestickService(
        quoteService = quoteService,
        candlestickRepository = candlestickRepository,
        instrumentService = instrumentService,
        candlestickProperties = candlestickProperties,
    )

    @Test
    fun `saves candlestick based on found quotes`() {
        val date = LocalDateTime.parse("2024-01-17T15:20:40").atZone(UTC).toInstant()
        val instrument = dummyInstrument(isin = "1")
        val quote1 = dummyQuote(isin = instrument.isin, price = 1.2, timestamp = date.beginningOfLastMinute())
        val quote2 = dummyQuote(isin = instrument.isin, price = 9.2, timestamp = date.beginningOfLastMinute().plusSeconds(5))
        val quote3 = dummyQuote(isin = instrument.isin, price = 0.9, timestamp = date.beginningOfLastMinute().plusSeconds(20))
        val quote4 = dummyQuote(isin = instrument.isin, price = 7.0, timestamp = date.beginningOfLastMinute().plusSeconds(55))

        every {
            quoteService.findQuotesByIsinWithinTimeFrame(isin = instrument.isin, from = any(), to = any())
        } returns listOf(quote1, quote2, quote3, quote4)

        candlestickService.saveCandlestick(instrument)

        verify(exactly = 1) {
            candlestickRepository.save(
                Candlestick(
                    isin = instrument.isin,
                    openTimestamp = LocalDateTime.parse("2024-01-17T15:19:00").atZone(UTC).toInstant(),
                    closeTimestamp = LocalDateTime.parse("2024-01-17T15:20:00").atZone(UTC).toInstant(),
                    openPrice = quote1.price,
                    highPrice = quote2.price,
                    lowPrice = quote3.price,
                    closingPrice = quote4.price,
                ),
            )
        }
    }

    @Test
    fun `does not saves candlestick if quote list is empty`() {
        val instrument = dummyInstrument(isin = "1")

        every {
            quoteService.findQuotesByIsinWithinTimeFrame(isin = instrument.isin, from = any(), to = any())
        } returns emptyList()

        candlestickService.saveCandlestick(instrument)

        verify(exactly = 0) { candlestickRepository.save(any()) }
    }

    @Test
    fun `returns list of candlestickDTO when isin is correct`() {
        val instrument = dummyInstrument(isin = "1")
        val candlestick1 = dummyCandlestick(isin = instrument.isin)
        val candlestick2 = dummyCandlestick(isin = instrument.isin)
        val candlestick3 = dummyCandlestick(isin = instrument.isin)
        val candlesticks = listOf(candlestick3, candlestick2, candlestick1)

        every {
            candlestickRepository.findByIsin(isin = instrument.isin, limit = candlestickProperties.maxPageSize)
        } returns candlesticks
        every { instrumentService.existsByIsin(instrument.isin) } returns true

        val expectedResult = candlesticks.map { it.toDTO() }

        val candlestickDTO = candlestickService.getCandlesticks(isin = instrument.isin)

        assertThat(candlestickDTO).isEqualTo(expectedResult)
    }

    @Test
    fun `throws exception when isin is incorrect`() {
        val isin = "1"
        every { instrumentService.existsByIsin(isin) } returns false

        assertThrows<IsinNotFoundException> { candlestickService.getCandlesticks(isin = isin) }
    }
}
