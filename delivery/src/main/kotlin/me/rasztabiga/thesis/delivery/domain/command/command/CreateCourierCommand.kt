package me.rasztabiga.thesis.delivery.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class CreateCourierCommand(
    @TargetAggregateIdentifier val id: String,
    val name: String
)
