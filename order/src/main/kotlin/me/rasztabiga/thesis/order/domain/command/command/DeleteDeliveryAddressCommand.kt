package me.rasztabiga.thesis.order.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class DeleteDeliveryAddressCommand(
    @TargetAggregateIdentifier val userId: String,
    val addressId: UUID
)
