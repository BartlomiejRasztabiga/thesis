package me.rasztabiga.thesis.restaurant.domain.command.event

import me.rasztabiga.thesis.restaurant.domain.command.aggregate.Availability
import org.axonframework.serialization.Revision
import java.util.*

@Revision("1.0")
data class RestaurantAvailabilityUpdatedEvent(
    val id: UUID,
    val availability: Availability
)
