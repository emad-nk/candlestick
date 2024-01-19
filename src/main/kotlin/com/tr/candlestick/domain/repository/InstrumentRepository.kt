package com.tr.candlestick.domain.repository

import com.tr.candlestick.configuration.CacheNames.INSTRUMENT_EXISTS_BY_ISIN
import com.tr.candlestick.domain.model.Instrument
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import javax.transaction.Transactional

@Repository
interface InstrumentRepository : JpaRepository<Instrument, String> {

    @CacheEvict(value = [INSTRUMENT_EXISTS_BY_ISIN], key = "{#entity.isin}")
    override fun <S : Instrument> save(entity: S): S

    @Modifying
    @Query(
        nativeQuery = true,
        value = """
        update instrument
        set scheduled_to = to_timestamp(:upToTime, 'YYYY-MM-DD HH24:MI:ss') + (interval '1' SECOND) * :intervalToAddInSeconds
        where isin in (
            select isin
            from instrument
            where scheduled_to < :upToTime
            order by scheduled_to
            limit :limit
            for update skip locked
        )
        returning *;
    """,
    )
    fun findScheduledInstruments(
        upToTime: Instant,
        intervalToAddInSeconds: Int,
        limit: Int,
    ): List<Instrument>

    @CacheEvict(INSTRUMENT_EXISTS_BY_ISIN, key = "{#isin}")
    @Transactional
    fun deleteByIsin(isin: String)

    @Cacheable(INSTRUMENT_EXISTS_BY_ISIN, key = "{#isin}")
    fun existsByIsin(isin: String): Boolean
}
