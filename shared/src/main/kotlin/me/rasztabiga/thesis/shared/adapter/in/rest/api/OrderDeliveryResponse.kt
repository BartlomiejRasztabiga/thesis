package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.math.BigDecimal
import java.util.*

data class OrderDeliveryResponse(
    val id: UUID,
    val restaurantLocation: Location,
    val deliveryLocation: Location,
    val status: DeliveryStatus,
    val courierFee: BigDecimal
) {
    enum class DeliveryStatus {
        OFFER,
        ACCEPTED,
        PICKED_UP,
        DELIVERED
    }
}
