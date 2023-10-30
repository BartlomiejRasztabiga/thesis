package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.time.Instant
import java.util.*

data class RestaurantOrderResponse(
    val restaurantOrderId: UUID,
    val orderId: UUID,
    val items: Map<UUID, Int>,
    val status: RestaurantOrderStatus,
    val createdAt: Instant
) {
    enum class RestaurantOrderStatus {
        NEW, ACCEPTED, PREPARED, REJECTED, PICKED_UP, DELIVERED
    }
}
