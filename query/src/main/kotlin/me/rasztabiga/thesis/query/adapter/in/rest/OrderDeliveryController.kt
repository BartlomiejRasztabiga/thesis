@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.query.adapter.`in`.rest

import me.rasztabiga.thesis.query.domain.query.query.FindAllDeliveriesByCourierId
import me.rasztabiga.thesis.query.domain.query.query.FindCurrentDeliveryQuery
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderDeliveryResponse
import me.rasztabiga.thesis.shared.config.getUserId
import me.rasztabiga.thesis.shared.security.Scopes.COURIER
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v2/deliveries")
class OrderDeliveryController(
    private val reactorQueryGateway: ReactorQueryGateway
) {

    @GetMapping("/current")
    @PreAuthorize("hasAnyAuthority('${COURIER.READ}')")
    fun getCurrentDelivery(
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<OrderDeliveryResponse>> {
        return reactorQueryGateway.query(
            FindCurrentDeliveryQuery(
                courierId = exchange.getUserId()
            ), ResponseTypes.instanceOf(OrderDeliveryResponse::class.java)
        )
            .map { ResponseEntity.ok(it) }
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('${COURIER.READ}')")
    fun getAllDeliveries(
        exchange: ServerWebExchange
    ): Mono<List<OrderDeliveryResponse>> {
        return reactorQueryGateway.query(
            FindAllDeliveriesByCourierId(
                courierId = exchange.getUserId()
            ), ResponseTypes.multipleInstancesOf(OrderDeliveryResponse::class.java)
        )
    }
}
