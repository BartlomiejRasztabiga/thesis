package me.rasztabiga.thesis.gateway

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.RestController

@RestController
@EnableConfigurationProperties(UriConfiguration::class)
class GatewayConfiguration {
    @Bean
    fun routes(builder: RouteLocatorBuilder, uriConfiguration: UriConfiguration): RouteLocator {
        return builder.routes()
            .route("restaurant") {
                it.path("/api/v1/restaurants/**")
                    .uri(uriConfiguration.restaurant)
            }
            .build()
    }
}
