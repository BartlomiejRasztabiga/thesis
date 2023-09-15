package me.rasztabiga.thesis.payment.adapter.out.stripe

import com.stripe.Stripe
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import me.rasztabiga.thesis.payment.domain.command.port.PaymentSessionPort
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderPaymentCommand
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Profile("stripe")
@Service
class StripePaymentSessionAdapter : PaymentSessionPort {
    override fun createPaymentSession(command: CreateOrderPaymentCommand): String {
        Stripe.apiKey =
            "sk_test_51NkBWgEKNDTQpRbz21IxN1lSre63zv95KLm787PdcaZRMWv0YhjTqiwVHqlB9CLGtV8BCv4VeaFUdG4SAAXLrBGH00kaWNjf8C"

        val domain = "http://localhost:3080"

        val params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("$domain/ordering/orders/${command.orderId}/tracking?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl("$domain/ordering/orders/${command.orderId}/canceled")
            .addAllLineItem(
                command.items.map {
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(it.quantity.toLong())
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("PLN")
                                .setUnitAmount(it.unitPrice.toLong())
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(it.name)
                                        .build()
                                )
                                .build()
                        )
                        .build()
                }.plus(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("PLN")
                                .setUnitAmount(command.deliveryFee.toLong())
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Delivery fee")
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
            )
            .build()

        val session = Session.create(params)
        return session.url
    }
}
