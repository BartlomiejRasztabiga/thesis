package me.rasztabiga.thesis.restaurant.domain.command.event

import me.rasztabiga.thesis.restaurant.domain.command.aggregate.Availability
import org.axonframework.serialization.Revision
import java.util.UUID

@Revision("2.0")
data class RestaurantCreatedEvent(
    val id: UUID,
    val name: String,
    val availability: Availability
)
