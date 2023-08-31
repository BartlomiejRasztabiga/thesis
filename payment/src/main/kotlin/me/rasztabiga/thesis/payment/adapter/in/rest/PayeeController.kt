@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.payment.adapter.`in`.rest

import me.rasztabiga.thesis.payment.adapter.`in`.rest.api.WithdrawBalanceRequest
import me.rasztabiga.thesis.payment.adapter.`in`.rest.mapper.PayeeControllerMapper.mapToWithdrawBalanceCommand
import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/payees")
class PayeeController(
    private val reactorCommandGateway: ReactorCommandGateway
) {

    @PutMapping("/{payeeId}/withdraw")
    @PreAuthorize("hasAnyAuthority('${Scopes.USER.WRITE}')")
    fun withdrawBalance(
        @PathVariable payeeId: UUID,
        @RequestBody request: WithdrawBalanceRequest,
        exchange: ServerWebExchange
    ): Mono<UuidWrapper> {
        val command = mapToWithdrawBalanceCommand(payeeId, request, exchange)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }
}
