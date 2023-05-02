package me.rasztabiga.thesis.restaurant.domain.command.event

import org.axonframework.serialization.Revision
import java.util.*

@Revision("1.0")
data class RestaurantCreatedEvent(
    val id: UUID,
    val name: String
)
