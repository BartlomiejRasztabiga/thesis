package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class CreatePayeeCommand(
    @TargetAggregateIdentifier val id: String,
)
