@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.query.adapter.`in`.rest

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.shared.config.getUserId
import me.rasztabiga.thesis.shared.domain.query.query.FindOrderByIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindOrdersByUserIdQuery
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v2/orders")
class OrderController(
    private val reactorQueryGateway: ReactorQueryGateway
) {

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyAuthority('${Scopes.ORDER.READ}')")
    fun getOrder(@PathVariable orderId: UUID): Mono<OrderResponse> {
        return reactorQueryGateway.query(
            FindOrderByIdQuery(orderId),
            ResponseTypes.instanceOf(OrderResponse::class.java)
        )
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('${Scopes.ORDER.READ}')")
    fun getOrders(exchange: ServerWebExchange): Mono<List<OrderResponse>> {
        return reactorQueryGateway.query(
            FindOrdersByUserIdQuery(exchange.getUserId()),
            ResponseTypes.multipleInstancesOf(OrderResponse::class.java)
        )
    }
}


