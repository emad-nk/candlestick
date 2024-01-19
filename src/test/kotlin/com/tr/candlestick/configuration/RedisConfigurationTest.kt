package com.tr.candlestick.configuration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RedisConfigurationTest {

    @Test
    fun `has the right number of caches`() {
        assertThat(CacheNames.all())
            .hasSize(2)
            .contains("candlesticks-by-isin")
    }
}
