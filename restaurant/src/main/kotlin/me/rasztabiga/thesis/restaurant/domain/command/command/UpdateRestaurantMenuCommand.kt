package me.rasztabiga.thesis.restaurant.domain.command.command

import me.rasztabiga.thesis.restaurant.domain.command.aggregate.Product
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class UpdateRestaurantMenuCommand(
    @TargetAggregateIdentifier val id: UUID,
    val menu: List<Product>
)
