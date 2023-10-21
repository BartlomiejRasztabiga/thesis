package me.rasztabiga.thesis.payment.adapter.out.fake

import me.rasztabiga.thesis.payment.domain.command.port.PaymentSessionPort
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderPaymentCommand
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Profile("!stripe")
@Service
class FakePaymentSessionAdapter : PaymentSessionPort {

    override fun createPaymentSession(command: CreateOrderPaymentCommand): String {
        return "https://example.com"
    }
}
