@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.payment.adapter.`in`.rest

import com.stripe.model.checkout.Session
import com.stripe.net.Webhook
import me.rasztabiga.thesis.payment.adapter.`in`.rest.mapper.PaymentControllerMapper.mapToPayPaymentCommand
import me.rasztabiga.thesis.payment.domain.command.command.PayPaymentCommand
import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val reactorCommandGateway: ReactorCommandGateway
) {

    // TODO temporary solution, until we integrate with payment gateway (e.g. Stripe)
    @PutMapping("/{paymentId}/pay")
    @PreAuthorize("hasAnyAuthority('${Scopes.USER.WRITE}')")
    fun payPayment(
        @PathVariable paymentId: UUID,
        exchange: ServerWebExchange
    ): Mono<UuidWrapper> {
        val command = mapToPayPaymentCommand(paymentId, exchange)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }

    @PostMapping("/stripe-webhook")
    fun stripeWebhook(@RequestBody body: String, exchange: ServerWebExchange) {
        val endpointSecret = "whsec_gwW0toQxHqnHkA6q93OrlyXeoBOl6NJE" // TODO move to secrets
        val sigHeader: String = exchange.request.headers["Stripe-Signature"]!![0]
        val event = Webhook.constructEvent(body, sigHeader, endpointSecret)

        if (event.type == "checkout.session.completed") {
            val session = event.dataObjectDeserializer.`object`.get() as Session
            val paymentId = UUID.fromString(session.clientReferenceId)
            val command = PayPaymentCommand(
                paymentId = paymentId
            )
            reactorCommandGateway.send<UUID>(command).block()
        }
    }
}
