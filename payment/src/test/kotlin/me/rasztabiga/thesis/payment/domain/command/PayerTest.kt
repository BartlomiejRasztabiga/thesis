package me.rasztabiga.thesis.payment.domain.command

import me.rasztabiga.thesis.payment.domain.command.aggregate.Payer
import me.rasztabiga.thesis.shared.domain.command.command.CreatePayerCommand
import me.rasztabiga.thesis.shared.domain.command.event.PayerCreatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class PayerTest {

    private lateinit var testFixture: AggregateTestFixture<Payer>

    @BeforeEach
    fun setUp() {
        testFixture = AggregateTestFixture(Payer::class.java)
    }

    @Test
    fun `should create payer`() {
        val createPayerCommand = CreatePayerCommand(
            id = UUID.randomUUID(),
            userId = "userId",
        )

        val payerCreatedEvent = PayerCreatedEvent(
            id = createPayerCommand.id,
            userId = createPayerCommand.userId,
        )

        testFixture.givenNoPriorActivity()
            .`when`(createPayerCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(payerCreatedEvent)
    }
}
