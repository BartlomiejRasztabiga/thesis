package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision
import java.util.*

@Revision("1.0")
data class RestaurantOrderCreatedEvent(
    val restaurantOrderId: UUID,
    val orderId: UUID,
    val items: Map<UUID, Int>,
    val restaurantId: UUID
)
