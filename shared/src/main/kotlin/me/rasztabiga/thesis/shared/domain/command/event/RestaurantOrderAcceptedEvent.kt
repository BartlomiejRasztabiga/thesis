package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision
import java.util.UUID

@Revision("1.0")
data class RestaurantOrderAcceptedEvent(
    val orderId: UUID,
    val restaurantId: UUID
)
