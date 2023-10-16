package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class CreateRestaurantOrderCommand(
    @TargetAggregateIdentifier val restaurantOrderId: UUID,
    val orderId: UUID,
    val restaurantId: UUID,
    val items: Map<UUID, Int>
)

