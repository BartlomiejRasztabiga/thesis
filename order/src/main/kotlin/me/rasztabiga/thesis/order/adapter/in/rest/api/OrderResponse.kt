@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest.api

import java.math.BigDecimal
import java.util.*

data class OrderResponse(
    val id: UUID,
    val restaurantId: UUID,
    val userId: String,
    val status: OrderStatus,
    val items: List<OrderItem>,
    val total: BigDecimal?,
    val paymentId: UUID?,
    val deliveryAddressId: UUID
) {
    enum class OrderStatus {
        CREATED,
        CANCELED,
        FINALIZED,
        PAID,
        CONFIRMED,
        REJECTED,
        PREPARED
    }

    // TODO maybe we should aggregate all the product data here (price etc.)
    data class OrderItem(
        val id: UUID,
        val productId: UUID
    )
}
