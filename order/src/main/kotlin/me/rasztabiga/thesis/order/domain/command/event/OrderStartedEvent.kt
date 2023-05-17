package me.rasztabiga.thesis.order.domain.command.event

import me.rasztabiga.thesis.order.domain.command.aggregate.OrderStatus
import org.axonframework.serialization.Revision
import java.util.*

@Revision("2.0")
data class OrderStartedEvent(
    val orderId: UUID,
    val status: OrderStatus
)
