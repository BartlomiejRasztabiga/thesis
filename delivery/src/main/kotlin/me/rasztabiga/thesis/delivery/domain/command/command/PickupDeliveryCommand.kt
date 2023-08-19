package me.rasztabiga.thesis.delivery.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

// TODO verify that restaurant order is prepared
data class PickupDeliveryCommand(
    @TargetAggregateIdentifier val id: UUID,
    val courierId: String
)
