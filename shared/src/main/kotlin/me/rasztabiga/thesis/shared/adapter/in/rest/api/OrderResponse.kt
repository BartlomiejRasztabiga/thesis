@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class OrderResponse(
    val id: UUID,
    val restaurantId: UUID,
    val restaurantLocation: Location,
    val userId: String,
    val status: OrderStatus,
    val items: Map<UUID, Int>,
    val total: BigDecimal?,
    val itemsTotal: BigDecimal?,
    val paymentId: UUID?,
    val paymentSessionUrl: String?,
    val deliveryAddressId: UUID?,
    val deliveryLocation: Location?,
    val deliveryFee: BigDecimal?,
    val courierId: String?,
    val courierLocation: Location?,
    val createdAt: Instant
) {
    enum class OrderStatus {
        CREATED,
        CANCELED,
        FINALIZED,
        PAID,
        CONFIRMED,
        REJECTED,
        PREPARED,
        PICKED_UP,
        DELIVERED
    }
}
