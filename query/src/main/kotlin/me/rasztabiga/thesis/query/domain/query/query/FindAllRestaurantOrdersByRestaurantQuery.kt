package me.rasztabiga.thesis.query.domain.query.query

import java.util.*

data class FindAllRestaurantOrdersByRestaurantQuery(
    val restaurantId: UUID
)
