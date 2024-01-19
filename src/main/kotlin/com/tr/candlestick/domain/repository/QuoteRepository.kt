package com.tr.candlestick.domain.repository

import com.tr.candlestick.domain.model.ISIN
import com.tr.candlestick.domain.model.Quote
import com.tr.candlestick.domain.model.QuoteId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface QuoteRepository : JpaRepository<Quote, QuoteId> {

    @Query(
        nativeQuery = true,
        value = """
            select * from quote
            where isin = :isin
            and timestamp >= :from
            and timestamp < :to
            order by timestamp
        """,
    )
    fun findQuotesByIsinWithinTimeFrame(
        isin: ISIN,
        from: Instant,
        to: Instant,
    ): List<Quote>
}
