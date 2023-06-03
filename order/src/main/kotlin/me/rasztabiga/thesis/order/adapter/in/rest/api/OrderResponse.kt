@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest.api

import java.util.*

data class OrderResponse(
    val id: UUID,
    val restaurantId: UUID,
    val userId: String,
    val status: OrderStatus
) {
    enum class OrderStatus {
        CREATED
    }
}
