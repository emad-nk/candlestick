package com.tr.candlestick.service

import com.tr.candlestick.domain.repository.InstrumentRepository
import com.tr.candlestick.dummyInstrument
import com.tr.candlestick.property.InstrumentProperties
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

class InstrumentServiceTest {

    private val instrumentRepository = mockk<InstrumentRepository>(relaxed = true)
    private val instrumentProperties = InstrumentProperties(maxPageSize = 500, secondsToAdd = 45)
    private val instrumentService = InstrumentService(instrumentRepository, instrumentProperties)

    @Test
    fun `saves an instrument`(){
        val instrument = dummyInstrument()
        every { instrumentRepository.save(instrument) } returns instrument

        assertThat(instrumentService.save(instrument)).isEqualTo(instrument)
    }

    @Test
    fun `deletes an instrument`(){
        val instrument = dummyInstrument()
        every { instrumentRepository.existsByIsin(instrument.isin) } returns true

        instrumentService.delete(instrument)

        verify(exactly = 1) { instrumentRepository.deleteByIsin(instrument.isin) }
    }

    @Test
    fun `does not delete an instrument when it does not exist`(){
        val instrument = dummyInstrument()
        every { instrumentRepository.existsByIsin(instrument.isin) } returns false

        instrumentService.delete(instrument)

        verify(exactly = 0) { instrumentRepository.deleteByIsin(instrument.isin) }
    }

    @Test
    fun `checks if an instrument exists by isin`(){
        val instrument = dummyInstrument()
        every { instrumentRepository.existsByIsin(instrument.isin) } returns true

        assertThat(instrumentService.existsByIsin(instrument.isin)).isTrue()

        verify(exactly = 1) { instrumentRepository.existsByIsin(instrument.isin) }
    }
}
