package com.tr.candlestick.service

import com.tr.candlestick.common.beginningOfCurrentMinute
import com.tr.candlestick.common.beginningOfLastMinute
import com.tr.candlestick.common.nowAtMillis
import com.tr.candlestick.domain.repository.QuoteRepository
import com.tr.candlestick.dummyQuote
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class QuoteServiceTest {
    private val quoteRepository = mockk<QuoteRepository>(relaxed = true)
    private val quoteService = QuoteService(quoteRepository)

    @Test
    fun `saves a quote`() {
        val quote = dummyQuote()
        every { quoteRepository.save(quote) } returns quote

        assertThat(quoteService.save(quote)).isEqualTo(quote)
    }

    @Test
    fun `gets quotes by isin and provided time range`() {
        val now = nowAtMillis()
        val quote = dummyQuote(isin = "1")
        every {
            quoteRepository.findQuotesByIsinWithinTimeFrame(
                isin = "1",
                from = now.beginningOfLastMinute(),
                to = now.beginningOfCurrentMinute(),
            )
        } returns listOf(quote)

        quoteService.findQuotesByIsinWithinTimeFrame(isin = "1", from = now.beginningOfLastMinute(), to = now.beginningOfCurrentMinute())

        verify(exactly = 1) {
            quoteRepository.findQuotesByIsinWithinTimeFrame(
                isin = "1",
                from = now.beginningOfLastMinute(),
                to = now.beginningOfCurrentMinute(),
            )
        }
    }
}
