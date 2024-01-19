package com.tr.candlestick.common

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZoneOffset.UTC
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TimeExtensionsKtTest{

    @Test
    fun `gets beginning of last minute`() {
        val date = LocalDateTime.parse("2024-01-16T15:20:40").atZone(UTC).toInstant()
        val expectedDate = LocalDateTime.parse("2024-01-16T15:19:00").atZone(UTC).toInstant()

        assertThat(date.beginningOfLastMinute()).isEqualTo(expectedDate)
    }

    @Test
    fun `gets beginning of current minute`() {
        val date = LocalDateTime.parse("2024-01-16T15:20:40").atZone(UTC).toInstant()
        val expectedDate = LocalDateTime.parse("2024-01-16T15:20:00").atZone(UTC).toInstant()

        assertThat(date.beginningOfCurrentMinute()).isEqualTo(expectedDate)
    }

    @Test
    fun `gets beginning of next minute`() {
        val date = LocalDateTime.parse("2024-01-16T15:20:59.45").atZone(UTC).toInstant()
        val expectedDate = LocalDateTime.parse("2024-01-16T15:21:00.00").atZone(UTC).toInstant()

        assertThat(date.beginningOfNextMinute()).isEqualTo(expectedDate)
    }
}
