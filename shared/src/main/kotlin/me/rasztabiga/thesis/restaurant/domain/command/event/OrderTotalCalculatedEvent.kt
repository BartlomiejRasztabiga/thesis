package me.rasztabiga.thesis.restaurant.domain.command.event

import org.axonframework.serialization.Revision
import java.math.BigDecimal
import java.util.*

@Revision("1.0")
data class OrderTotalCalculatedEvent(
    val orderId: UUID,
    val total: BigDecimal
)
