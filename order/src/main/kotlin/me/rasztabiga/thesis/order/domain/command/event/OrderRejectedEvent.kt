package me.rasztabiga.thesis.order.domain.command.event

import org.axonframework.serialization.Revision
import java.util.*

@Revision("1.0")
data class OrderRejectedEvent(
    val orderId: UUID
)