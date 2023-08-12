package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api

import java.util.*

data class RestaurantOrderResponse(
    val restaurantOrderId: UUID,
    val items: List<OrderItem>,
    val status: RestaurantOrderStatus
) {
    enum class RestaurantOrderStatus {
        NEW, ACCEPTED, PREPARED
    }
}
