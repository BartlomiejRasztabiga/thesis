package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class CreatePayeeCommand(
    @TargetAggregateIdentifier val id: UUID,
    val userId: String
)
