package com.tr.candlestick.common

import java.time.Instant
import java.time.Instant.now
import java.time.temporal.ChronoUnit.MILLIS
import java.time.temporal.ChronoUnit.MINUTES

fun nowAtMillis(): Instant = now().truncatedTo(MILLIS)

fun Instant.beginningOfLastMinute(): Instant = this.truncatedTo(MINUTES).minus(1, MINUTES)
fun Instant.beginningOfCurrentMinute(): Instant = this.truncatedTo(MINUTES)
fun Instant.beginningOfNextMinute(): Instant = this.truncatedTo(MINUTES).plus(1, MINUTES)
