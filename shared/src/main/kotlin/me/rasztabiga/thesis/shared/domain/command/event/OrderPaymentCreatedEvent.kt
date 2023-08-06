package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision
import java.math.BigDecimal
import java.util.*

@Revision("1.0")
data class OrderPaymentCreatedEvent(
    val id: UUID,
    val orderId: UUID,
    val payeeId: String,
    val amount: BigDecimal
)
