package me.rasztabiga.thesis.restaurant.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class PrepareRestaurantOrderCommand(
    @TargetAggregateIdentifier val restaurantOrderId: UUID,
    val restaurantId: UUID
)
