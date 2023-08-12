package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class CreateRestaurantOrderCommand(
    @TargetAggregateIdentifier val restaurantOrderId: UUID,
    val restaurantId: UUID,
    val items: List<OrderItem>
) {
    data class OrderItem(
        val productId: UUID
    )
}

