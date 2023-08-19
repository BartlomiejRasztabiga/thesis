package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision
import java.math.BigDecimal
import java.util.*

@Revision("1.0")
data class OrderDeliveryPickedUpEvent(
    val deliveryId: UUID,
    val orderId: UUID,
    val courierId: String
)
