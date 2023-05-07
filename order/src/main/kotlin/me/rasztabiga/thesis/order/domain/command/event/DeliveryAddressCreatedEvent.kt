package me.rasztabiga.thesis.order.domain.command.event

import org.axonframework.serialization.Revision
import java.util.UUID

@Revision("1.0")
data class DeliveryAddressCreatedEvent(
    val userId: String,
    val addressId: UUID,
    val address: String,
    val additionalInfo: String?
)
