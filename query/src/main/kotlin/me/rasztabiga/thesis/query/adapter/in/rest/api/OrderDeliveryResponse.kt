package me.rasztabiga.thesis.query.adapter.`in`.rest.api

import java.math.BigDecimal
import java.util.*

data class OrderDeliveryResponse(
    val id: UUID,
    val restaurantAddress: String,
    val deliveryAddress: String,
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
