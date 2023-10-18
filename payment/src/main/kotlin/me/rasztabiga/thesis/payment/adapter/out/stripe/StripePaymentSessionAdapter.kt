package me.rasztabiga.thesis.payment.adapter.out.stripe

import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import me.rasztabiga.thesis.payment.domain.command.port.PaymentSessionPort
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderPaymentCommand
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Profile("stripe")
@Service
class StripePaymentSessionAdapter(
    @Value("\${stripe.frontend.domain}")
    private val stripeFrontendDomain: String
) : PaymentSessionPort {
    override fun createPaymentSession(command: CreateOrderPaymentCommand): String {
        val params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("$stripeFrontendDomain/v2/ordering/orders/${command.orderId}/tracking")
            .setCancelUrl("$stripeFrontendDomain/v2/ordering/orders/${command.orderId}/payment")
            .addAllLineItem(
                command.items.map {
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(it.quantity.toLong())
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("PLN")
                                .setUnitAmount(it.unitPrice.times(CENTS).toLong())
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
                                .setUnitAmount(command.deliveryFee.times(CENTS).toLong())
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
            .setClientReferenceId(command.id.toString())
            .build()

        val session = Session.create(params)
        return session.url
    }

    companion object {
        private val CENTS = 100.toBigDecimal()
    }
}
