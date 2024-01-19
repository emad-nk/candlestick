package com.tr.candlestick.domain.repository

import com.tr.candlestick.IntegrationTestParent
import com.tr.candlestick.common.nowAtMillis
import com.tr.candlestick.configuration.CacheNames.CANDLESTICKS_BY_ISIN
import com.tr.candlestick.domain.model.Candlestick
import com.tr.candlestick.dummyCandlestick
import com.tr.candlestick.dummyInstrument
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate

class CandlestickRepositoryIT(
    @Autowired private val candlestickRepository: CandlestickRepository,
    @Autowired private val instrumentRepository: InstrumentRepository,
    @Autowired private val redisTemplate: RedisTemplate<String, String>,
) : IntegrationTestParent() {

    @Test
    fun `gets latest candlestick ordered by latest first and limited to specified number`() {
        val instrument1 = dummyInstrument(isin = "1")
        val instrument2 = dummyInstrument(isin = "2")
        val candlestick1 = dummyCandlestick(isin = instrument1.isin, openTimestamp = nowAtMillis().minusSeconds(120))
        val candlestick2 = dummyCandlestick(isin = instrument1.isin, openTimestamp = nowAtMillis().minusSeconds(100))
        val candlestick3 = dummyCandlestick(isin = instrument1.isin, openTimestamp = nowAtMillis().minusSeconds(80))
        val candlestick4 = dummyCandlestick(isin = instrument1.isin, openTimestamp = nowAtMillis().minusSeconds(70))
        val candlestick5 = dummyCandlestick(isin = instrument2.isin, openTimestamp = nowAtMillis().minusSeconds(60))

        instrumentRepository.saveAll(listOf(instrument1, instrument2))
        candlestickRepository.saveAll(listOf(candlestick1, candlestick2, candlestick3, candlestick4, candlestick5))

        val foundCandlesticks = candlestickRepository.findByIsin(isin = instrument1.isin, limit = 3)

        assertThat(foundCandlesticks).hasSize(3)
        assertThat(foundCandlesticks).containsExactly(candlestick4, candlestick3, candlestick2)
    }

    @Test
    fun `caches the result and evicts the cache when new candlestick related to the same isin is saved`() {
        val instrument1 = dummyInstrument(isin = "1")
        val instrument2 = dummyInstrument(isin = "2")
        val candlestick1 = dummyCandlestick(isin = instrument1.isin, openTimestamp = nowAtMillis().minusSeconds(120))
        val candlestick2 = dummyCandlestick(isin = instrument1.isin, openTimestamp = nowAtMillis().minusSeconds(100))
        val candlestick3 = dummyCandlestick(isin = instrument1.isin, openTimestamp = nowAtMillis().minusSeconds(80))
        val candlestick4 = dummyCandlestick(isin = instrument1.isin, openTimestamp = nowAtMillis().minusSeconds(70))
        val candlestick5 = dummyCandlestick(isin = instrument2.isin, openTimestamp = nowAtMillis().minusSeconds(60))

        instrumentRepository.saveAll(listOf(instrument1, instrument2))
        candlestickRepository.saveAll(listOf(candlestick1, candlestick2, candlestick3, candlestick4, candlestick5))

        val foundCandlesticks1 = candlestickRepository.findByIsin(isin = instrument1.isin, limit = 3)
        val foundCandlesticks2 = candlestickRepository.findByIsin(isin = instrument2.isin, limit = 3)

        assertThat(foundCandlesticks1).hasSize(3)
        assertThat(foundCandlesticks1).containsExactly(candlestick4, candlestick3, candlestick2)
        assertThat(redisTemplate.keys("$CANDLESTICKS_BY_ISIN::1")).hasSize(1)

        assertThat(foundCandlesticks2).hasSize(1)
        assertThat(foundCandlesticks2).containsExactly(candlestick5)
        assertThat(redisTemplate.keys("$CANDLESTICKS_BY_ISIN::2")).hasSize(1)

        candlestickRepository.save(dummyCandlestick(isin = instrument1.isin))
        assertThat(redisTemplate.keys("$CANDLESTICKS_BY_ISIN::1")).isEmpty()
        assertThat(redisTemplate.keys("$CANDLESTICKS_BY_ISIN::2")).hasSize(1)
    }
}
