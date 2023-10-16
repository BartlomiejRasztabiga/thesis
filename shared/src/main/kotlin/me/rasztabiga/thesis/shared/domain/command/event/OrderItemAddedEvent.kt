package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision
import java.util.*

@Revision("1.0")
data class OrderItemAddedEvent(
    val orderId: UUID,
    val productId: UUID
)
