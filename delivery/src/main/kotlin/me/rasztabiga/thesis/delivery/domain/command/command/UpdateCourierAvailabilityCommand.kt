package me.rasztabiga.thesis.delivery.domain.command.command

import me.rasztabiga.thesis.delivery.domain.command.aggregate.Availability
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class UpdateCourierAvailabilityCommand(
    @TargetAggregateIdentifier val id: String,
    val availability: Availability
)
