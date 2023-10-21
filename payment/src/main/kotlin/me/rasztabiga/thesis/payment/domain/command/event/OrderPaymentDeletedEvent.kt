package me.rasztabiga.thesis.payment.domain.command.event

import org.axonframework.serialization.Revision
import java.util.*

@Revision("1.0")
data class OrderPaymentDeletedEvent(
    val id: UUID
)
