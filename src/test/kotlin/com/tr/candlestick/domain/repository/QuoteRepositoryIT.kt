package com.tr.candlestick.domain.repository

import com.tr.candlestick.IntegrationTestParent
import com.tr.candlestick.common.beginningOfCurrentMinute
import com.tr.candlestick.common.beginningOfLastMinute
import com.tr.candlestick.common.nowAtMillis
import com.tr.candlestick.dummyInstrument
import com.tr.candlestick.dummyQuote
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class QuoteRepositoryIT(
    @Autowired private val quoteRepository: QuoteRepository,
    @Autowired private val instrumentRepository: InstrumentRepository,
) : IntegrationTestParent() {

    @Test
    fun `finds quotes by isin with specified time range`() {
        val now = nowAtMillis()
        val instrument1 = dummyInstrument(isin = "1")
        val instrument2 = dummyInstrument(isin = "2")
        val quote1 = dummyQuote(isin = instrument1.isin, timestamp = now.beginningOfLastMinute().plusSeconds(1), price = 9.0)
        val quote2 = dummyQuote(isin = instrument1.isin, timestamp = now.beginningOfLastMinute().plusSeconds(2), price = 5.4)
        val quote3 = dummyQuote(isin = instrument1.isin, timestamp = now.beginningOfLastMinute().plusSeconds(3), price = 15.0)
        val quote4 = dummyQuote(isin = instrument1.isin, timestamp = now.beginningOfLastMinute().plusSeconds(4), price = 4.9)
        val quote5 = dummyQuote(isin = instrument2.isin, timestamp = now.beginningOfLastMinute().plusSeconds(4), price = 24.9)

        instrumentRepository.saveAll(listOf(instrument1, instrument2))
        quoteRepository.saveAll(listOf(quote2, quote3, quote4, quote5, quote1))

        val foundQuotes = quoteRepository.findQuotesByIsinWithinTimeFrame(
            isin = instrument1.isin,
            from = now.beginningOfLastMinute(),
            to = now.beginningOfCurrentMinute()
        )

        assertThat(foundQuotes).hasSize(4)
        assertThat(foundQuotes).containsExactly(quote1, quote2, quote3, quote4)
    }
}
