package com.tr.candlestick.controller

import com.tr.candlestick.controller.dto.CandlestickDTO
import com.tr.candlestick.controller.dto.ResponseListDTO
import com.tr.candlestick.service.CandlestickManager
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/candlesticks")
@Tag(name = "Candlesticks Controller", description = "Returns a list of candlestick by ISIN")
class CandlestickController(
    private val candlestickManager: CandlestickManager,
) {

    @Operation(description = "Returns a list of candlesticks by ISIN for the past 30 minutes")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "List of candlestick with their properties"),
        ],
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(code = OK)
    fun getCountries(
        @RequestParam isin: String,
    ): ResponseListDTO<CandlestickDTO> = ResponseListDTO(
        items = candlestickManager.getCandlesticks(isin),
    )
}
