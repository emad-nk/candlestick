package com.tr.candlestick

import org.springframework.boot.builder.SpringApplicationBuilder

/**
 * Entry point for starting application locally.
 * This is mostly helpful if Testcontainers are used instead of docker-compose-local
 */
class StartApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder(CandlestickApplication::class.java)
                .profiles("local")
                .initializers(Initializer())
                .run(*args)
        }
    }
}
