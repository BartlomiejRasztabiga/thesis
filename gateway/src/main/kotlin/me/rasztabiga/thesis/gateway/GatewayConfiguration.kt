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
            .route("query") {
                it.method("GET")
                    .and()
                    .path("/api/v1/**")
                    .uri(uriConfiguration.query)
            }
            .route("restaurant") {
                it.path("/api/v1/restaurants/**")
                    .uri(uriConfiguration.restaurant)
            }
            .route("order") {
                it.path("/api/v1/orders/**")
                    .uri(uriConfiguration.order)
            }
            .route("user") {
                it.path("/api/v1/users/**")
                    .uri(uriConfiguration.order)
            }
            .route("payment") {
                it.path("/api/v1/payments/**")
                    .uri(uriConfiguration.payment)
            }
            .route("payee") {
                it.path("/api/v1/payees/**")
                    .uri(uriConfiguration.payment)
            }
            .route("delivery") {
                it.path("/api/v1/deliveries/**")
                    .uri(uriConfiguration.delivery)
            }
            .route("courier") {
                it.path("/api/v1/couriers/**")
                    .uri(uriConfiguration.delivery)
            }
            .build()
    }
}
