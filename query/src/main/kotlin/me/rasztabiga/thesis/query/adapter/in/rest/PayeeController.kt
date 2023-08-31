@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.query.adapter.`in`.rest

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.PayeeResponse
import me.rasztabiga.thesis.shared.config.getUserId
import me.rasztabiga.thesis.shared.domain.query.query.FindPayeeByUserIdQuery
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/payees")
class PayeeController(
    private val reactorQueryGateway: ReactorQueryGateway
) {

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('${Scopes.USER.READ}')")
    fun getCurrentPayee(
        exchange: ServerWebExchange
    ): Mono<PayeeResponse> {
        return reactorQueryGateway.query(
            FindPayeeByUserIdQuery(exchange.getUserId()),
            ResponseTypes.instanceOf(PayeeResponse::class.java)
        )
    }
}
