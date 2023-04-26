package me.rasztabiga.thesis.restaurant.domain.command.command

import me.rasztabiga.thesis.restaurant.domain.command.aggregate.Availability
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class UpdateRestaurantAvailabilityCommand(
    @TargetAggregateIdentifier val id: UUID,
    val availability: Availability
)
