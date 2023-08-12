package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api

import java.util.*

data class RestaurantOrderResponse(
    val orderId: UUID,
    val items: List<OrderItem>,
    val status: RestaurantOrderStatus
) {
    enum class RestaurantOrderStatus {
        NEW, ACCEPTED, PREPARED
    }
}
