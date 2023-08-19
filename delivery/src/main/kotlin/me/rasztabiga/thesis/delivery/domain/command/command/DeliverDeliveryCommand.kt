package me.rasztabiga.thesis.delivery.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class DeliverDeliveryCommand(
    @TargetAggregateIdentifier val id: UUID,
    val courierId: String
)
