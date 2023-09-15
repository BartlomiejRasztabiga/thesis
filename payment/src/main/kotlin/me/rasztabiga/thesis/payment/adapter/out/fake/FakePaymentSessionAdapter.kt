package me.rasztabiga.thesis.payment.adapter.out.fake

import me.rasztabiga.thesis.payment.domain.command.port.PaymentSessionPort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.net.URL

@Profile("!stripe")
@Service
class FakePaymentSessionAdapter : PaymentSessionPort {
    override fun createPaymentSession(): URL {
        return URL("https://example.com")
    }
}
