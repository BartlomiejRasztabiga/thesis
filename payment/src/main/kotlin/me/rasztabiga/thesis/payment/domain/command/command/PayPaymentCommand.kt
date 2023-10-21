package me.rasztabiga.thesis.payment.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class PayPaymentCommand(
    @TargetAggregateIdentifier val paymentId: UUID
)
