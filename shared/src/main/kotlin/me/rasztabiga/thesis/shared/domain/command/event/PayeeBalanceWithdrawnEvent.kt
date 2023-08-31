package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision
import java.math.BigDecimal
import java.util.*

@Revision("1.0")
data class PayeeBalanceWithdrawnEvent(
    val payeeId: UUID,
    val amount: BigDecimal,
    val targetBankAccount: String
)
