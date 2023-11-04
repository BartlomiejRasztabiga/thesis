package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@Revision("1.0")
data class InvoiceCreatedEvent(
    val invoiceId: UUID,
    val from: String,
    val to: String,
    val issueDate: LocalDate,
    val dueDate: LocalDate,
    val items: List<InvoiceItem>,
    val amountPaid: String?
) {
    data class InvoiceItem(
        val name: String,
        val quantity: Int,
        val unitPrice: BigDecimal
    )
}

