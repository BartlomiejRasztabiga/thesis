package me.rasztabiga.thesis.query.adapter.`in`.rest.api

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import java.math.BigDecimal
import java.util.*

data class OrderDeliveryResponse(
    val id: UUID,
    val restaurantLocation: Location,
    val distanceToRestaurantInKm: Double?,
    val deliveryLocation: Location,
    val distanceToDeliveryAddressInKm: Double?,
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
