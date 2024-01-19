package com.tr.candlestick.stream

import com.tr.candlestick.domain.model.InstrumentEvent.Type.ADD
import com.tr.candlestick.domain.model.InstrumentEvent.Type.DELETE
import com.tr.candlestick.service.InstrumentService
import com.tr.candlestick.service.QuoteService
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/**
 * Populates Instrument and Quote tables
 */

@Component
@ConditionalOnProperty("websocket.enabled", havingValue = "true")
class StreamPopulator(
    private val instrumentStream: InstrumentStream,
    private val quoteStream: QuoteStream,
    private val instrumentService: InstrumentService,
    private val quoteService: QuoteService,
) {
    @PostConstruct
    fun startStreamsAfterStartup() {
        logger.info { "starting up" }

        processInstrumentStreams()
        processQuoteStreams()
    }

    private fun processInstrumentStreams() {
        instrumentStream.connect { event ->
            when (event.type) {
                ADD -> instrumentService.save(event.data)
                DELETE -> instrumentService.delete(event.data)
            }
            logger.debug { "Instrument: $event" }
        }
    }

    private fun processQuoteStreams() {
        quoteStream.connect { event ->
            quoteService.save(event.data)
            logger.debug { "Quote: $event" }
        }
    }

    companion object {
        val logger = KotlinLogging.logger { }
    }
}
