package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class AssignDeliveryCommand(
    @TargetAggregateIdentifier val id: UUID,
    val courierId: String
)
