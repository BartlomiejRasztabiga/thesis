package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class CalculateOrderTotalCommand(
    val orderId: UUID,
    @TargetAggregateIdentifier val restaurantId: UUID,
    val items: List<OrderItem>
) {
    data class OrderItem(
        val orderItemId: UUID,
        val productId: UUID
    )
}