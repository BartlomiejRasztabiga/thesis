@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.payment.adapter.`in`.rest

import com.stripe.model.checkout.Session
import com.stripe.net.Webhook
import me.rasztabiga.thesis.payment.domain.command.command.PayPaymentCommand
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import java.util.*

@Profile("stripe")
@RestController
@RequestMapping("/api/v1/payments")
class StripePaymentController(
    private val commandGateway: CommandGateway,
    @Value("\${stripe.endpoint.secret}")
    private val stripeEndpointSecret: String
) {

    @PostMapping("/stripe-webhook")
    fun stripeWebhook(@RequestBody body: String, exchange: ServerWebExchange) {
        val sigHeader: String = exchange.request.headers["Stripe-Signature"]!![0]
        val event = Webhook.constructEvent(body, sigHeader, stripeEndpointSecret)

        if (event.type == "checkout.session.completed") {
            val session = event.dataObjectDeserializer.`object`.get() as Session
            val paymentId = UUID.fromString(session.clientReferenceId)
            val command = PayPaymentCommand(
                paymentId = paymentId
            )
            commandGateway.send<UUID>(command).join()
        }
    }
}
