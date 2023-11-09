package me.rasztabiga.thesis.shared.domain.command.event

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.axonframework.serialization.Revision
import java.math.BigDecimal
import java.util.*

@Revision("1.0")
data class OrderDeliveryRejectedEvent(
    val deliveryId: UUID,
    val courierId: String,
    val restaurantLocation: Location
)
