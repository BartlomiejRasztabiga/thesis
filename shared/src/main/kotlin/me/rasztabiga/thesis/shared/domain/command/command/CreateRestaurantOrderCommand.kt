package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class CreateRestaurantOrderCommand(
    @TargetAggregateIdentifier val orderId: UUID,
    val items: List<OrderItem>
) {
    data class OrderItem(
        val productId: UUID
    )
}

