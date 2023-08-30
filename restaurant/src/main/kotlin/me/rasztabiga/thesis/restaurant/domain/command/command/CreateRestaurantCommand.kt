package me.rasztabiga.thesis.restaurant.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class CreateRestaurantCommand(
    @TargetAggregateIdentifier val id: UUID,
    val managerId: String,
    val email: String,
    val name: String,
    val address: String
)
