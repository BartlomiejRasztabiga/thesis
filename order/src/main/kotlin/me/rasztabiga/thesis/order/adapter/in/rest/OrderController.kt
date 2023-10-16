@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.AddOrderItemRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.DeleteOrderItemRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.FinalizeOrderRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.StartOrderRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.OrderControllerMapper.mapToAddOrderItemCommand
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.OrderControllerMapper.mapToCancelOrderCommand
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.OrderControllerMapper.mapToDeleteOrderItemCommand
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.OrderControllerMapper.mapToFinalizeOrderCommand
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.OrderControllerMapper.mapToStartOrderCommand
import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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
    private val reactorCommandGateway: ReactorCommandGateway
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

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('${Scopes.ORDER.WRITE}')")
    fun cancelOrder(
        @PathVariable orderId: UUID,
        exchange: ServerWebExchange
    ): Mono<Void> {
        val command = mapToCancelOrderCommand(orderId, exchange)
        return reactorCommandGateway.send(command)
    }

    @PutMapping("/{orderId}/finalize")
    @PreAuthorize("hasAnyAuthority('${Scopes.ORDER.WRITE}')")
    fun finalizeOrder(
        @PathVariable orderId: UUID,
        @RequestBody request: FinalizeOrderRequest,
        exchange: ServerWebExchange
    ): Mono<UuidWrapper> {
        val command = mapToFinalizeOrderCommand(orderId, request, exchange)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }

    @PostMapping("/{orderId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('${Scopes.ORDER.WRITE}')")
    fun addOrderItem(
        @PathVariable orderId: UUID,
        @RequestBody request: AddOrderItemRequest,
        exchange: ServerWebExchange
    ): Mono<UuidWrapper> {
        val command = mapToAddOrderItemCommand(orderId, request, exchange)
        return reactorCommandGateway.send<UUID>(command)
            .then(Mono.just(UuidWrapper(orderId)))
    }

    @DeleteMapping("/{orderId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('${Scopes.ORDER.WRITE}')")
    fun deleteOrderItem(
        @PathVariable orderId: UUID,
        @RequestBody request: DeleteOrderItemRequest,
        exchange: ServerWebExchange
    ): Mono<Void> {
        val command = mapToDeleteOrderItemCommand(orderId, request, exchange)
        return reactorCommandGateway.send(command)
    }
}


