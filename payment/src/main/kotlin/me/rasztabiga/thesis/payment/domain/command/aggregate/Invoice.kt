package me.rasztabiga.thesis.payment.domain.command.aggregate

import me.rasztabiga.thesis.payment.domain.command.port.InvoiceGenerationPort
import me.rasztabiga.thesis.shared.domain.command.command.CreateInvoiceCommand
import me.rasztabiga.thesis.shared.domain.command.event.InvoiceCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
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

    @CommandHandler
    constructor(command: CreateInvoiceCommand, invoiceGenerationPort: InvoiceGenerationPort) {

        // TODO or generate only when sending email (so that we won't have to store pdf in db)

        // TODO generation port
        val pdf = invoiceGenerationPort.generate(
            InvoiceGenerationPort.InvoiceData(
                id = command.id,
                from = command.from,
                to = command.to,
                issueDate = command.issueDate,
                dueDate = command.dueDate,
                items = command.items.map {
                    InvoiceGenerationPort.InvoiceData.InvoiceItem(
                        name = it.name,
                        quantity = it.quantity,
                        unitPrice = it.unitPrice
                    )
                }
            ))

        // TODO add pdf here

        apply(
            InvoiceCreatedEvent(
                id = command.id,
                from = command.from,
                to = command.to,
                issueDate = command.issueDate,
                dueDate = command.dueDate,
                items = command.items.map {
                    InvoiceCreatedEvent.InvoiceItem(
                        name = it.name,
                        quantity = it.quantity,
                        unitPrice = it.unitPrice
                    )
                }
            )
        )
    }

    @EventSourcingHandler
    fun on(event: InvoiceCreatedEvent) {
        this.id = event.id
        this.from = event.from
        this.to = event.to
        this.issueDate = event.issueDate
        this.dueDate = event.dueDate
        this.items = event.items.map {
            InvoiceItem(
                name = it.name,
                quantity = it.quantity,
                unitPrice = it.unitPrice
            )
        }
    }
}