package com.tr.candlestick.service

import com.tr.candlestick.domain.model.ISIN
import com.tr.candlestick.domain.model.Quote
import com.tr.candlestick.domain.repository.QuoteRepository
import com.tr.candlestick.exception.SQLException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class QuoteService(
    private val quoteRepository: QuoteRepository,
) {

    fun save(quote: Quote): Quote {
        return try {
            quoteRepository.save(quote)
        } catch (ex: DataIntegrityViolationException) {
            throw SQLException("Foreign key constraint violation - ISIN ${quote.isin} does not exist in instrument table")
        }
    }

    fun findQuotesByIsinWithinTimeFrame(
        isin: ISIN,
        from: Instant,
        to: Instant,
    ) = quoteRepository.findQuotesByIsinWithinTimeFrame(
        isin = isin,
        from = from,
        to = to,
    )
}
