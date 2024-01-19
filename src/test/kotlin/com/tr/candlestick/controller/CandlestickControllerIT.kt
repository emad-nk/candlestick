package com.tr.candlestick.controller

import com.tr.candlestick.IntegrationTestParent
import com.tr.candlestick.asJson
import com.tr.candlestick.common.nowAtMillis
import com.tr.candlestick.configuration.CacheNames.CANDLESTICKS_BY_ISIN
import com.tr.candlestick.configuration.jackson
import com.tr.candlestick.controller.dto.CandlestickDTO
import com.tr.candlestick.controller.dto.ResponseListDTO
import com.tr.candlestick.domain.model.Candlestick
import com.tr.candlestick.domain.model.toDTO
import com.tr.candlestick.domain.repository.CandlestickRepository
import com.tr.candlestick.domain.repository.InstrumentRepository
import com.tr.candlestick.dummyCandlestick
import com.tr.candlestick.dummyInstrument
import io.mockk.verify
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType.JSON
import org.apache.http.HttpStatus.SC_NOT_FOUND
import org.apache.http.HttpStatus.SC_OK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate

class CandlestickControllerIT(
    @Autowired private val candlestickRepository: CandlestickRepository,
    @Autowired private val instrumentRepository: InstrumentRepository,
    @Autowired private val redisTemplate: RedisTemplate<String, String>,
) : IntegrationTestParent() {

    @BeforeAll
    fun init() {
        RestAssured.port = localPort
    }

    @Test
    fun `retrieves candlesticks and caches the result in redis`() {
        val now = nowAtMillis()
        val instrument1 = dummyInstrument(isin = "1")
        val instrument2 = dummyInstrument(isin = "2")
        val candlestick1 = dummyCandlestick(isin = instrument1.isin, openTimestamp = now.minusSeconds(200))
        val candlestick2 = dummyCandlestick(isin = instrument1.isin, openTimestamp = now.minusSeconds(250))
        val candlestick3 = dummyCandlestick(isin = instrument1.isin, openTimestamp = now.minusSeconds(180))
        val candlestick4 = dummyCandlestick(isin = instrument1.isin, openTimestamp = now.minusSeconds(100))
        val candlestick5 = dummyCandlestick(isin = instrument1.isin, openTimestamp = now.minusSeconds(150))
        val candlestick6 = dummyCandlestick(isin = instrument2.isin, openTimestamp = now.minusSeconds(50))
        val expectedCandlesticks = listOf(candlestick4, candlestick5, candlestick3, candlestick1, candlestick2).map { it.toDTO() }

        instrumentRepository.saveAll(listOf(instrument1, instrument2))
        candlestickRepository.saveAll(listOf(candlestick1, candlestick2, candlestick3, candlestick4, candlestick5, candlestick6))

        assertThat(redisTemplate.keys("$CANDLESTICKS_BY_ISIN*")).isEmpty()
        val candlesticks: ResponseListDTO<CandlestickDTO> = given()
            .contentType(JSON)
            .param("isin", "1")
            .`when`()
            .get(CANDLESTICK_URI)
            .then()
            .log().ifValidationFails()
            .statusCode(SC_OK)
            .extract()
            .body()
            .asJson(jackson)

        assertThat(candlesticks.items).hasSize(5)
        assertThat(candlesticks.items).containsExactlyElementsOf(expectedCandlesticks)
        assertThat(redisTemplate.keys("$CANDLESTICKS_BY_ISIN*")).hasSize(1)
    }

    @Test
    fun `receives not found exception when isin does not exist`() {
        given()
            .contentType(JSON)
            .param("isin", "1")
            .`when`()
            .get(CANDLESTICK_URI)
            .then()
            .log().ifValidationFails()
            .statusCode(SC_NOT_FOUND)
    }

    companion object {
        private const val CANDLESTICK_URI = "/api/v1/candlesticks"
    }
}
