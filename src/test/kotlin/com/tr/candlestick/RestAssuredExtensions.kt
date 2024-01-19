package com.tr.candlestick

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.restassured.response.ResponseBodyData

inline fun <reified T> ResponseBodyData.asJson(objectMapper: ObjectMapper): T =
    objectMapper.readValue(this.asString())
