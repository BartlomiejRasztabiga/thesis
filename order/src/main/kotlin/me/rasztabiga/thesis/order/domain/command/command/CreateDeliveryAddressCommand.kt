package me.rasztabiga.thesis.order.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class CreateDeliveryAddressCommand(
    @TargetAggregateIdentifier val userId: String,
    val addressId: UUID,
    val address: String,
    val additionalInfo: String?
)
