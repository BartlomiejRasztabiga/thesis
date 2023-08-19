package me.rasztabiga.thesis.delivery.domain.command.event

import org.axonframework.serialization.Revision
import java.math.BigDecimal
import java.util.*

@Revision("1.0")
data class OrderDeliveryAcceptedEvent(
    val deliveryId: UUID,
    val courierId: String
)
