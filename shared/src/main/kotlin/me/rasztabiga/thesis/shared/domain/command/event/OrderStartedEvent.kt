package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision
import java.util.*

@Revision("1.0")
data class OrderStartedEvent(
    val orderId: UUID,
    val restaurantId: UUID,
    val userId: String,
    val status: OrderStatus
) {
    enum class OrderStatus {
        CREATED,
        CANCELED,
        FINALIZED,
        PAID,
        REJECTED
    }
}
