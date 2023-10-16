package me.rasztabiga.thesis.order.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class AddOrderItemCommand(
    @TargetAggregateIdentifier val orderId: UUID,
    val userId: String,
    val productId: UUID
)
