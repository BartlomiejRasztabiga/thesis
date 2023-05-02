package me.rasztabiga.thesis.order.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class CreateUserCommand(
    @TargetAggregateIdentifier val id: String,
    val name: String
)
