package me.rasztabiga.thesis.restaurant.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class AcceptRestaurantOrderCommand(
    @TargetAggregateIdentifier val restaurantOrderId: UUID,
    val restaurantId: UUID
)
