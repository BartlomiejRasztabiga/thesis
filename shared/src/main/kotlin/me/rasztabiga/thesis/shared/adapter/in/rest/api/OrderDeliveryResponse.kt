package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class OrderDeliveryResponse(
    val id: UUID,
    val orderId: UUID,
    val restaurantLocation: Location,
    val distanceToRestaurantInKm: Double,
    val deliveryLocation: Location,
    val distanceToDeliveryAddressInKm: Double,
    val status: DeliveryStatus,
    val courierFee: BigDecimal,
    val createdAt: Instant
) {
    enum class DeliveryStatus {
        OFFER,
        ASSIGNED,
        ACCEPTED,
        PICKED_UP,
        DELIVERED
    }
}
