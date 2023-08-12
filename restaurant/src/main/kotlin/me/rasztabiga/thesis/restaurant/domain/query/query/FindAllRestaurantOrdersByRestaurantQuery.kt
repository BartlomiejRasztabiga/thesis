package me.rasztabiga.thesis.restaurant.domain.query.query

import java.util.*

data class FindAllRestaurantOrdersByRestaurantQuery(
    val restaurantId: UUID
)
