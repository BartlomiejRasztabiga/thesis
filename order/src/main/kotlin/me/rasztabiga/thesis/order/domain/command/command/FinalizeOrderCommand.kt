package me.rasztabiga.thesis.order.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class FinalizeOrderCommand(
    @TargetAggregateIdentifier val orderId: UUID,
    val userId: String
)
