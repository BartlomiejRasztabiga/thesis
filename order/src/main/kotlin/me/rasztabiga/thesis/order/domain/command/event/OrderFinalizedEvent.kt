package me.rasztabiga.thesis.order.domain.command.event

import org.axonframework.serialization.Revision
import java.util.*

@Revision("3.0")
data class OrderFinalizedEvent(
    val orderId: UUID,
    val userId: String,
    val restaurantId: UUID,
    val items: List<OrderItem>,
    val deliveryAddressId: UUID
) {
    data class OrderItem(
        val orderItemId: UUID,
        val productId: UUID
    )
}
