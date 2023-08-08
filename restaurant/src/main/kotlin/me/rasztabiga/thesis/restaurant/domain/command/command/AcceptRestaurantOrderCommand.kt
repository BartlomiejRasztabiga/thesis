package me.rasztabiga.thesis.restaurant.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class AcceptRestaurantOrderCommand(
    @TargetAggregateIdentifier val orderId: UUID,
    val restaurantId: UUID
    // TODO userId?
)
