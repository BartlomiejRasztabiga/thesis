package me.rasztabiga.thesis.payment.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class DeleteRestaurantCommand(
    @TargetAggregateIdentifier val id: UUID
)
