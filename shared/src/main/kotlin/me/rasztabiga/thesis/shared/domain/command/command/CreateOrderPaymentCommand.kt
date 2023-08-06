package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.math.BigDecimal
import java.util.*

data class CreateOrderPaymentCommand(
    @TargetAggregateIdentifier val id: UUID,
    val orderId: UUID,
    val payeeId: String,
    val amount: BigDecimal
)
