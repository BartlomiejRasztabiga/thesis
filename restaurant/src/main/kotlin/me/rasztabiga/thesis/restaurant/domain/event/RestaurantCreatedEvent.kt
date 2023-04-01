package me.rasztabiga.thesis.restaurant.domain.event

import java.util.UUID

data class RestaurantCreatedEvent(
    val id: UUID,
    val name: String
)
