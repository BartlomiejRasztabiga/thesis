package me.rasztabiga.thesis.payment.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class UpdateRestaurantCommand(
    @TargetAggregateIdentifier val id: UUID,
    val name: String
)
