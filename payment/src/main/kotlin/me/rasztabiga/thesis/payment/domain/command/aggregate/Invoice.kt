package me.rasztabiga.thesis.payment.domain.command.aggregate

import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.time.LocalDate
import java.util.*

@Aggregate
class Invoice {

    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var from: String
    private lateinit var to: String
    private lateinit var issueDate: LocalDate
    private lateinit var dueDate: LocalDate
    private lateinit var items: List<InvoiceItem>

    data class InvoiceItem(
        val name: String,
        val quantity: Int,
        val unitPrice: Double
    )

    private constructor()
}
