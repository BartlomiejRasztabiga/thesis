package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class DeleteOrderPaymentCommand(
    @TargetAggregateIdentifier val paymentId: UUID
)
