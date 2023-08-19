package me.rasztabiga.thesis.delivery.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class RejectDeliveryOfferCommand(
    @TargetAggregateIdentifier val id: UUID,
    val courierId: String
)
