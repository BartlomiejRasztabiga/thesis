@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.query.adapter.`in`.rest

import me.rasztabiga.thesis.query.adapter.`in`.rest.api.CourierResponse
import me.rasztabiga.thesis.query.domain.query.query.FindCourierByIdQuery
import me.rasztabiga.thesis.shared.config.getUserId
import me.rasztabiga.thesis.shared.security.Scopes.COURIER
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/couriers")
class CourierController(
    private val reactorQueryGateway: ReactorQueryGateway
) {
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('${COURIER.READ}')")
    fun getCurrentCourier(exchange: ServerWebExchange): Mono<CourierResponse> {
        return reactorQueryGateway.query(
            FindCourierByIdQuery(exchange.getUserId()),
            ResponseTypes.instanceOf(CourierResponse::class.java)
        )
    }
}
