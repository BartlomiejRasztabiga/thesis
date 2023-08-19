@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.delivery.adapter.`in`.rest

import me.rasztabiga.thesis.delivery.adapter.`in`.rest.api.OrderDeliveryResponse
import me.rasztabiga.thesis.delivery.domain.query.query.FindSuitableDeliveryOfferQuery
import me.rasztabiga.thesis.shared.config.getUserId
import me.rasztabiga.thesis.shared.security.Scopes.COURIER
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/deliveries")
class OrderDeliveryController(
    private val reactorCommandGateway: ReactorCommandGateway,
    private val reactorQueryGateway: ReactorQueryGateway
) {
    @GetMapping("/offer")
    @PreAuthorize("hasAnyAuthority('${COURIER.READ}')")
    fun getSuitableDeliveryOffer(
        @RequestParam courierAddress: String,
        exchange: ServerWebExchange
    ): Mono<OrderDeliveryResponse> {
        return reactorQueryGateway.query(
            FindSuitableDeliveryOfferQuery(
                courierId = exchange.getUserId(),
                courierAddress = courierAddress
            ),
            ResponseTypes.instanceOf(OrderDeliveryResponse::class.java)
        )
    }
}
