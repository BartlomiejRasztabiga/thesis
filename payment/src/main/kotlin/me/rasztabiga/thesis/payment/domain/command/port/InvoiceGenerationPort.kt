package me.rasztabiga.thesis.payment.domain.command.port

import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

interface InvoiceGenerationPort {

    fun generate(invoiceData: InvoiceData): ByteArray?

    data class InvoiceData(
        val id: UUID,
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
}
