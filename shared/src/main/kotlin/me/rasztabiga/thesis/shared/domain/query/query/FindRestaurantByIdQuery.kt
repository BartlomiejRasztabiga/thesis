package me.rasztabiga.thesis.shared.domain.query.query

import java.util.*

data class FindRestaurantByIdQuery(
    val restaurantId: UUID
)