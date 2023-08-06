package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class CreateRestaurantOrderCommand(
    @TargetAggregateIdentifier val orderId: UUID
)
