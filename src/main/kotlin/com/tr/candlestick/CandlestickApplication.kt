package com.tr.candlestick

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author Emad Nikkhouy
 */

@SpringBootApplication
class CandlestickApplication

fun main(args: Array<String>) {
    runApplication<CandlestickApplication>(*args)
}
