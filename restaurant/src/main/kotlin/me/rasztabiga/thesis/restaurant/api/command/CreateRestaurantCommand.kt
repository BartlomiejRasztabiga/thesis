package me.rasztabiga.thesis.restaurant.api.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class CreateRestaurantCommand(
    @TargetAggregateIdentifier val restaurantId: UUID,
    val name: String
)
