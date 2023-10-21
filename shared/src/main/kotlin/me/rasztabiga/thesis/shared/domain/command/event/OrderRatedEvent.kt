package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision
import java.util.UUID

@Revision("1.0")
data class OrderRatedEvent(
    val orderId: UUID,
    val userId: String,
    val rating: Int
)
