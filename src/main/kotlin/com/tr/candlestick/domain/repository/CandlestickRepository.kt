package com.tr.candlestick.domain.repository

import com.tr.candlestick.configuration.CacheNames.CANDLESTICKS_BY_ISIN
import com.tr.candlestick.domain.model.Candlestick
import com.tr.candlestick.domain.model.CandlestickId
import com.tr.candlestick.domain.model.ISIN
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CandlestickRepository : JpaRepository<Candlestick, CandlestickId> {

    @CacheEvict(value = [CANDLESTICKS_BY_ISIN], key = "{#entity.isin}")
    override fun <S : Candlestick> save(entity: S): S

    @Cacheable(CANDLESTICKS_BY_ISIN, key = "{#isin}")
    @Query(
        nativeQuery = true,
        value = """
            select * from candlestick
            where isin = :isin
            order by open_timestamp desc
            limit :limit
        """,
    )
    fun findByIsin(
        isin: ISIN,
        limit: Int,
    ): List<Candlestick>
}
