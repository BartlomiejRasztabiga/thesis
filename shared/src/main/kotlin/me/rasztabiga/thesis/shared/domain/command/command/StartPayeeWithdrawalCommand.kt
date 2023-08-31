package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.math.BigDecimal
import java.util.*

data class StartPayeeWithdrawalCommand(
    @TargetAggregateIdentifier val payeeId: UUID,
    val amount: BigDecimal,
    val targetBankAccount: String
)
