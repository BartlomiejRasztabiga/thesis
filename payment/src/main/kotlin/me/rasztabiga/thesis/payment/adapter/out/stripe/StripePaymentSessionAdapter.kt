package me.rasztabiga.thesis.payment.adapter.out.stripe

import com.stripe.param.checkout.SessionCreateParams
import me.rasztabiga.thesis.payment.domain.command.port.PaymentSessionPort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.net.URL
import java.util.*

@Profile("stripe")
@Service
class StripePaymentSessionAdapter : PaymentSessionPort {
    override fun createPaymentSession(orderId: UUID): URL {
        val apiKey =
            "sk_test_51NkBWgEKNDTQpRbz21IxN1lSre63zv95KLm787PdcaZRMWv0YhjTqiwVHqlB9CLGtV8BCv4VeaFUdG4SAAXLrBGH00kaWNjf8C"

        val domain = "http://localhost:3080"

        val params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("$domain/ordering/orders/$orderId/tracking?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl("$domain/ordering/orders/$orderId/canceled")
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("usd")
                            .setUnitAmount(2000L)
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("Stubborn Attachments")
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build()
    }
}
