package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.math.BigDecimal
import java.util.*

data class CreateOrderPaymentCommand(
    @TargetAggregateIdentifier val id: UUID,
    val orderId: UUID,
    val payerId: String,
    val amount: BigDecimal,
    val items: List<OrderItem>,
    val deliveryFee: BigDecimal
) {
    data class OrderItem(
        val name: String,
        val quantity: Int,
        val unitPrice: BigDecimal
    )
}
