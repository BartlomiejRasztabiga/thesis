package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision
import java.math.BigDecimal
import java.util.*

@Revision("1.0")
data class OrderTotalCalculatedEvent(
    val orderId: UUID,
    val restaurantId: UUID,
    val productsTotal: BigDecimal,
    val deliveryFee: BigDecimal,
    val items: Map<UUID, Int>
)
