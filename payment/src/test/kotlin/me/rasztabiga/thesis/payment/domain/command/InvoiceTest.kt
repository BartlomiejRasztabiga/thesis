package me.rasztabiga.thesis.payment.domain.command

import io.mockk.every
import io.mockk.mockk
import me.rasztabiga.thesis.payment.domain.command.aggregate.Invoice
import me.rasztabiga.thesis.payment.domain.command.port.EmailSendingPort
import me.rasztabiga.thesis.payment.domain.command.port.InvoiceGenerationPort
import me.rasztabiga.thesis.shared.domain.command.command.CreateInvoiceCommand
import me.rasztabiga.thesis.shared.domain.command.command.SendInvoiceEmailCommand
import me.rasztabiga.thesis.shared.domain.command.event.InvoiceCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.InvoiceEmailSentEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class InvoiceTest {

    private lateinit var testFixture: AggregateTestFixture<Invoice>
    private lateinit var invoiceGenerationPort: InvoiceGenerationPort
    private lateinit var emailSendingPort: EmailSendingPort

    @BeforeEach
    fun setUp() {
        invoiceGenerationPort = mockk<InvoiceGenerationPort>()
        emailSendingPort = mockk<EmailSendingPort>()

        testFixture = AggregateTestFixture(Invoice::class.java)
        testFixture.registerInjectableResource(invoiceGenerationPort)
        testFixture.registerInjectableResource(emailSendingPort)
    }

    @Test
    fun `should create invoice`() {
        val createInvoiceCommand = CreateInvoiceCommand(
            id = UUID.randomUUID(),
            from = "John Doe",
            to = "Jane Doe",
            issueDate = LocalDate.now(),
            dueDate = LocalDate.now().plusDays(30),
            items = listOf(
                CreateInvoiceCommand.InvoiceItem(
                    name = "Item 1",
                    quantity = 1,
                    unitPrice = BigDecimal.valueOf(100)
                ),
                CreateInvoiceCommand.InvoiceItem(
                    name = "Item 2",
                    quantity = 2,
                    unitPrice = BigDecimal.valueOf(200)
                )
            )
        )

        val invoiceCreatedEvent = InvoiceCreatedEvent(
            invoiceId = createInvoiceCommand.id,
            from = createInvoiceCommand.from,
            to = createInvoiceCommand.to,
            issueDate = createInvoiceCommand.issueDate,
            dueDate = createInvoiceCommand.dueDate,
            items = createInvoiceCommand.items.map {
                InvoiceCreatedEvent.InvoiceItem(
                    name = it.name,
                    quantity = it.quantity,
                    unitPrice = it.unitPrice
                )
            }
        )

        testFixture.givenNoPriorActivity()
            .`when`(createInvoiceCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(invoiceCreatedEvent)
    }

    @Test
    fun `given created invoice, should send invoice`() {
        val invoiceCreatedEvent = InvoiceCreatedEvent(
            invoiceId = UUID.randomUUID(),
            from = "John Doe",
            to = "Jane Doe",
            issueDate = LocalDate.now(),
            dueDate = LocalDate.now().plusDays(30),
            items = listOf(
                InvoiceCreatedEvent.InvoiceItem(
                    name = "Item 1",
                    quantity = 1,
                    unitPrice = BigDecimal.valueOf(100)
                ),
                InvoiceCreatedEvent.InvoiceItem(
                    name = "Item 2",
                    quantity = 2,
                    unitPrice = BigDecimal.valueOf(200)
                )
            )
        )

        val sendInvoiceEmailCommand = SendInvoiceEmailCommand(
            id = invoiceCreatedEvent.invoiceId,
            email = "email@example.com"
        )

        val pdf = "PDF".toByteArray()

        every { invoiceGenerationPort.generate(any()) } returns pdf
        every { emailSendingPort.send(sendInvoiceEmailCommand.email, pdf) } returns Unit

        val invoiceEmailSentEvent = InvoiceEmailSentEvent(
            invoiceId = sendInvoiceEmailCommand.id,
            email = sendInvoiceEmailCommand.email
        )

        testFixture.given(invoiceCreatedEvent)
            .`when`(sendInvoiceEmailCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(invoiceEmailSentEvent)
    }
}
