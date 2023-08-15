@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.delivery.adapter.`in`.rest

import me.rasztabiga.thesis.delivery.adapter.`in`.rest.api.CourierResponse
import me.rasztabiga.thesis.delivery.adapter.`in`.rest.api.CreateCourierRequest
import me.rasztabiga.thesis.delivery.adapter.`in`.rest.mapper.CourierControllerMapper.mapToCreateCourierCommand
import me.rasztabiga.thesis.delivery.domain.query.query.FindCourierByIdQuery
import me.rasztabiga.thesis.shared.StringIdWrapper
import me.rasztabiga.thesis.shared.config.getUserId
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/couriers")
class CourierController(
    private val reactorCommandGateway: ReactorCommandGateway,
    private val reactorQueryGateway: ReactorQueryGateway
) {
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('${Scopes.COURIER.READ}')")
    fun getCurrentCourier(exchange: ServerWebExchange): Mono<CourierResponse> {
        return reactorQueryGateway.query(
            FindCourierByIdQuery(exchange.getUserId()),
            ResponseTypes.instanceOf(CourierResponse::class.java)
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('${Scopes.COURIER.WRITE}')")
    fun createCourier(
        @RequestBody request: CreateCourierRequest,
        exchange: ServerWebExchange
    ): Mono<StringIdWrapper> {
        val command = mapToCreateCourierCommand(request, exchange)
        val id = reactorCommandGateway.send<String>(command)
        return id.map { StringIdWrapper(it) }
    }
}
