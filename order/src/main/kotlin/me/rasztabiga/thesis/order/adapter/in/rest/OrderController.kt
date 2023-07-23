@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.AddOrderItemRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.StartOrderRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.OrderControllerMapper.mapToAddOrderItemCommand
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.OrderControllerMapper.mapToDeleteOrderItemCommand
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.OrderControllerMapper.mapToStartOrderCommand
import me.rasztabiga.thesis.order.domain.query.query.FindOrderByIdQuery
import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val reactorCommandGateway: ReactorCommandGateway,
    private val reactorQueryGateway: ReactorQueryGateway
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('${Scopes.ORDER.WRITE}')")
    fun startOrder(
        @RequestBody request: StartOrderRequest,
        exchange: ServerWebExchange
    ): Mono<UuidWrapper> {
        val command = mapToStartOrderCommand(request, exchange)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyAuthority('${Scopes.ORDER.READ}')")
    fun getOrder(@PathVariable orderId: UUID): Mono<OrderResponse> {
        return reactorQueryGateway.query(
            FindOrderByIdQuery(orderId),
            ResponseTypes.instanceOf(OrderResponse::class.java)
        )
    }

    @PostMapping("/{orderId}/items")
    @PreAuthorize("hasAnyAuthority('${Scopes.ORDER.WRITE}')")
    fun addOrderItem(
        @PathVariable orderId: UUID,
        @RequestBody request: AddOrderItemRequest,
        exchange: ServerWebExchange
    ): Mono<UuidWrapper> {
        val command = mapToAddOrderItemCommand(orderId, request, exchange)
        return reactorCommandGateway.send<UUID>(command)
            .then(Mono.just(UuidWrapper(command.orderItemId)))
    }

    @DeleteMapping("/{orderId}/items/{orderItemId}")
    @PreAuthorize("hasAnyAuthority('${Scopes.ORDER.WRITE}')")
    fun deleteOrderItem(
        @PathVariable orderId: UUID,
        @PathVariable orderItemId: UUID,
        exchange: ServerWebExchange
    ): Mono<Void> {
        val command = mapToDeleteOrderItemCommand(orderId, orderItemId, exchange)
        return reactorCommandGateway.send(command)
    }
}


