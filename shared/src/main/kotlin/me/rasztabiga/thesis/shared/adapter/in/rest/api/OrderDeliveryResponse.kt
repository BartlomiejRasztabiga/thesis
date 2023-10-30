package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class OrderDeliveryResponse(
    val id: UUID,
    val orderId: UUID,
    val restaurantLocation: Location,
    val deliveryLocation: Location,
    val status: DeliveryStatus,
    val courierFee: BigDecimal,
    val createdAt: Instant,
    val courierIdsDeclined: List<String>,
) {
    enum class DeliveryStatus {
        OFFER,
        ACCEPTED,
        PICKED_UP,
        DELIVERED
    }
}
