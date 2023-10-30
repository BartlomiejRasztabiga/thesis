package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.math.BigDecimal
import java.util.*

data class OrderDeliveryOfferResponse(
    val id: UUID,
    val orderId: UUID,
    val restaurantLocation: Location,
    val distanceToRestaurantInKm: Double,
    val deliveryLocation: Location,
    val distanceToDeliveryAddressInKm: Double,
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
