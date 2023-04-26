package me.rasztabiga.thesis.restaurant.domain.command.event

import java.util.*

data class RestaurantDeletedEvent(
    val id: UUID
)
