package me.rasztabiga.thesis.restaurant.api.rest

import java.util.UUID

data class CreateRestaurantRequest(
    val restaurantId: UUID,
    val name: String
)
