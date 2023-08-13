package me.rasztabiga.thesis.restaurant.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class RejectRestaurantOrderCommand(
    @TargetAggregateIdentifier val restaurantOrderId: UUID
)
