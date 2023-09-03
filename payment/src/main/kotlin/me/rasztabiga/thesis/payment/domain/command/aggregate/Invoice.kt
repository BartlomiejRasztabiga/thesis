package me.rasztabiga.thesis.payment.domain.command.aggregate

import me.rasztabiga.thesis.payment.domain.command.port.EmailSendingPort
import me.rasztabiga.thesis.payment.domain.command.port.InvoiceGenerationPort
import me.rasztabiga.thesis.shared.domain.command.command.CreateInvoiceCommand
import me.rasztabiga.thesis.shared.domain.command.command.SendInvoiceEmailCommand
import me.rasztabiga.thesis.shared.domain.command.event.InvoiceCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.InvoiceEmailSentEvent
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
    private lateinit var status: InvoiceStatus

    data class InvoiceItem(
        val name: String,
        val quantity: Int,
        val unitPrice: Double
    )

    enum class InvoiceStatus {
        CREATED, SENT
    }

    private constructor()

    @CommandHandler
    constructor(command: CreateInvoiceCommand) {
        apply(
            InvoiceCreatedEvent(
                invoiceId = command.id,
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

    @CommandHandler
    fun handle(
        command: SendInvoiceEmailCommand,
        invoiceGenerationPort: InvoiceGenerationPort,
        emailSendingPort: EmailSendingPort
    ) {
        val pdf = invoiceGenerationPort.generate(
            InvoiceGenerationPort.InvoiceData(
                id = this.id,
                from = this.from,
                to = this.to,
                issueDate = this.issueDate,
                dueDate = this.dueDate,
                items = this.items.map {
                    InvoiceGenerationPort.InvoiceData.InvoiceItem(
                        name = it.name,
                        quantity = it.quantity,
                        unitPrice = it.unitPrice
                    )
                }
            ))

        emailSendingPort.send(command.email, pdf!!)

        apply(
            InvoiceEmailSentEvent(
                invoiceId = command.id,
                email = command.email
            )
        )
    }

    @EventSourcingHandler
    fun on(event: InvoiceCreatedEvent) {
        this.id = event.invoiceId
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
        this.status = InvoiceStatus.CREATED
    }

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: InvoiceEmailSentEvent) {
        this.status = InvoiceStatus.SENT
    }
}
