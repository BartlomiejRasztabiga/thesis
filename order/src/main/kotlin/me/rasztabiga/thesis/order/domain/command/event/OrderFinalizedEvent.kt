package me.rasztabiga.thesis.order.domain.command.event

import org.axonframework.serialization.Revision
import java.util.*

@Revision("2.0")
data class OrderFinalizedEvent(
    val orderId: UUID,
    val userId: String,
    val restaurantId: UUID,
    val items: List<OrderItem>
) {
    data class OrderItem(
        val orderItemId: UUID,
        val productId: UUID
    )
}
