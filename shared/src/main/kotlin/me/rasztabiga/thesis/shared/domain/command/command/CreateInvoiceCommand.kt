package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class CreateInvoiceCommand(
    @TargetAggregateIdentifier val id: UUID,
    val from: String,
    val to: String,
    val issueDate: LocalDate,
    val dueDate: LocalDate,
    val items: List<InvoiceItem>
) {
    data class InvoiceItem(
        val name: String,
        val quantity: Int,
        val unitPrice: BigDecimal
    )
}
