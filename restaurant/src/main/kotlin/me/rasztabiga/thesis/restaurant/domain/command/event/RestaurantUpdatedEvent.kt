package me.rasztabiga.thesis.restaurant.domain.command.event

import java.util.*

data class RestaurantUpdatedEvent(
    val id: UUID,
    val name: String
)
