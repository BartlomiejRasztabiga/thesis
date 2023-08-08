package me.rasztabiga.thesis.restaurant.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class PrepareRestaurantOrderCommand(
    @TargetAggregateIdentifier val orderId: UUID,
    val restaurantId: UUID
)
