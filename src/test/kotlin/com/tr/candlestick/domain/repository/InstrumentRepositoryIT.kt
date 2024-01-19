package com.tr.candlestick.domain.repository

import com.tr.candlestick.IntegrationTestParent
import com.tr.candlestick.common.beginningOfCurrentMinute
import com.tr.candlestick.common.nowAtMillis
import com.tr.candlestick.configuration.CacheNames.INSTRUMENT_EXISTS_BY_ISIN
import com.tr.candlestick.dummyInstrument
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate

class InstrumentRepositoryIT(
    @Autowired private val instrumentRepository: InstrumentRepository,
    @Autowired private val redisTemplate: RedisTemplate<String, String>,
) : IntegrationTestParent() {

    @Test
    fun `finds instruments that their scheduleTo is older than provided time and updates their scheduledTo to specified time`() {
        val now = nowAtMillis()
        val expectedScheduleTo = now.beginningOfCurrentMinute().plusSeconds(45)
        val instrument1 = dummyInstrument(isin = "1", scheduledTo = now.minusSeconds(50))
        val instrument2 = dummyInstrument(isin = "2", scheduledTo = now.minusSeconds(5))
        val instrument3 = dummyInstrument(isin = "3", scheduledTo = now.minusSeconds(70))
        val instrument4 = dummyInstrument(isin = "4", scheduledTo = now.minusSeconds(80))

        instrumentRepository.saveAll(listOf(instrument1, instrument2, instrument3, instrument4))

        val foundInstruments = instrumentRepository.findScheduledInstruments(
            upToTime = now.beginningOfCurrentMinute(),
            intervalToAddInSeconds = 45,
            limit = 2,
        )

        assertThat(foundInstruments).hasSize(2)
        assertThat(foundInstruments).containsExactlyInAnyOrder(
            instrument4.copy(scheduledTo = expectedScheduleTo),
            instrument3.copy(scheduledTo = expectedScheduleTo),
        )
    }

    @Test
    fun `saving an instrument evicts the INSTRUMENT_EXISTS_BY_ISIN cache`() {
        assertThat(instrumentRepository.existsByIsin("2")).isFalse()
        assertThat(redisTemplate.keys("$INSTRUMENT_EXISTS_BY_ISIN::2")).hasSize(1)

        instrumentRepository.save(dummyInstrument(isin = "2"))

        assertThat(redisTemplate.keys("$INSTRUMENT_EXISTS_BY_ISIN::2")).isEmpty()
    }

    @Test
    fun `deletes an instrument by isin and evicts the cache`() {
        val instrument1 = dummyInstrument(isin = "1")
        val instrument2 = dummyInstrument(isin = "2")

        instrumentRepository.saveAll(listOf(instrument1, instrument2))

        assertThat(instrumentRepository.existsByIsin(instrument2.isin)).isTrue()
        assertThat(redisTemplate.keys("$INSTRUMENT_EXISTS_BY_ISIN::2")).hasSize(1)

        instrumentRepository.deleteByIsin(isin = instrument2.isin)

        assertThat(instrumentRepository.findAll()).hasSize(1).contains(instrument1)
        assertThat(redisTemplate.keys("$INSTRUMENT_EXISTS_BY_ISIN::2")).isEmpty()
    }

    @ParameterizedTest
    @CsvSource(
        "1,true",
        "2,false",
    )
    fun `checks if instrument exists by isin`(isin: String, exist: Boolean) {
        val instrument = dummyInstrument(isin = "1")

        instrumentRepository.save((instrument))

        assertThat(instrumentRepository.existsByIsin(isin)).isEqualTo(exist)
    }
}
