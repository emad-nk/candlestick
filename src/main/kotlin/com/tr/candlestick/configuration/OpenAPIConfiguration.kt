package com.tr.candlestick.configuration

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAPIConfiguration {

    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .components(Components())
            .info(
                Info()
                    .title("candlestick")
                    .description(SERVICE_DESCRIPTION)
                    .version(API_VERSION),
            )

    companion object {
        private const val API_VERSION = "1.0"
        private const val SERVICE_DESCRIPTION = "Serves candlesticks for specific ISIN for the past 30 minutes"
    }
}
