package me.rasztabiga.thesis.restaurant.domain.command.event

import java.util.UUID

data class RestaurantCreatedEvent(
    val id: UUID,
    val name: String
)
