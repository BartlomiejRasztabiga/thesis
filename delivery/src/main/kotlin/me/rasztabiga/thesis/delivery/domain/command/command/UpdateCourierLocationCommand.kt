package me.rasztabiga.thesis.delivery.domain.command.command

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class UpdateCourierLocationCommand(
    @TargetAggregateIdentifier val id: String,
    val location: Location
)
