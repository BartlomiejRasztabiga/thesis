@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.StartOrderRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.OrderControllerMapper
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.OrderControllerMapper.mapToStartOrderCommand
import me.rasztabiga.thesis.order.domain.query.query.FindOrderByIdQuery
import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
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
    fun startOrder(@RequestBody request: StartOrderRequest): Mono<UuidWrapper> {
        // TODO userId powinno przyjsc z tokenu (???)
        val command = mapToStartOrderCommand(request)
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
}


