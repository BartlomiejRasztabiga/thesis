@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.payment.adapter.`in`.rest

import me.rasztabiga.thesis.payment.adapter.`in`.rest.mapper.PaymentControllerMapper.mapToPayPaymentCommand
import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val reactorCommandGateway: ReactorCommandGateway
) {

    @PutMapping("/{paymentId}/pay")
    @PreAuthorize("hasAnyAuthority('${Scopes.USER.WRITE}')")
    fun payPayment(
        @PathVariable paymentId: UUID
    ): Mono<UuidWrapper> {
        val command = mapToPayPaymentCommand(paymentId)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }
}
