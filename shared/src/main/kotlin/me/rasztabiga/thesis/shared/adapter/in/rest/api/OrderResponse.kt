@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.math.BigDecimal
import java.util.*

data class OrderResponse(
    val id: UUID,
    val restaurantId: UUID,
    val restaurantLocation: Location,
    val userId: String,
    val status: OrderStatus,
    val items: List<OrderItem>,
    val total: BigDecimal?,
    val paymentId: UUID?,
    val deliveryAddressId: UUID?,
    val deliveryLocation: Location?,
    val courierId: String?
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

    // TODO maybe we should aggregate all the product data here (price etc.)
    data class OrderItem(
        val id: UUID,
        val productId: UUID
    )
}
