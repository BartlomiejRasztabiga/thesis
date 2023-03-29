package me.rasztabiga.thesis.restaurant.api.event

import java.util.UUID

data class RestaurantCreatedEvent(
    val restaurantId: UUID,
    val name: String
)
