package me.rasztabiga.thesis.restaurant.domain.command.event

import me.rasztabiga.thesis.restaurant.domain.command.aggregate.OrderItem
import org.axonframework.serialization.Revision
import java.util.*

@Revision("2.0")
data class RestaurantOrderCreatedEvent(
    val restaurantOrderId: UUID,
    val orderId: UUID,
    val items: List<OrderItem>,
    val restaurantId: UUID
)
