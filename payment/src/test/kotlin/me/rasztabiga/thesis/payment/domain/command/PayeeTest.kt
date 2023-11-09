package me.rasztabiga.thesis.payment.domain.command

import me.rasztabiga.thesis.payment.domain.command.aggregate.Payee
import me.rasztabiga.thesis.shared.domain.command.command.AddPayeeBalanceCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreatePayeeCommand
import me.rasztabiga.thesis.shared.domain.command.command.WithdrawPayeeBalanceCommand
import me.rasztabiga.thesis.shared.domain.command.event.PayeeBalanceAddedEvent
import me.rasztabiga.thesis.shared.domain.command.event.PayeeBalanceWithdrawnEvent
import me.rasztabiga.thesis.shared.domain.command.event.PayeeCreatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class PayeeTest {

    private lateinit var testFixture: AggregateTestFixture<Payee>

    @BeforeEach
    fun setUp() {
        testFixture = AggregateTestFixture(Payee::class.java)
    }

    @Test
    fun `should create payee`() {
        val createPayeeCommand = CreatePayeeCommand(
            id = UUID.randomUUID(),
            userId = "userId",
            name = "name",
            email = "email",
            payeeType = CreatePayeeCommand.PayeeType.RESTAURANT_MANAGER
        )

        val payeeCreatedEvent = PayeeCreatedEvent(
            payeeId = createPayeeCommand.id,
            userId = createPayeeCommand.userId,
            name = createPayeeCommand.name,
            email = createPayeeCommand.email,
            payeeType = PayeeCreatedEvent.PayeeType.RESTAURANT_MANAGER
        )

        testFixture.givenNoPriorActivity()
            .`when`(createPayeeCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(payeeCreatedEvent)
    }

    @Test
    fun `given payee, should add balance`() {
        val payeeCreatedEvent = PayeeCreatedEvent(
            payeeId = UUID.randomUUID(),
            userId = "userId",
            name = "name",
            email = "email",
            payeeType = PayeeCreatedEvent.PayeeType.RESTAURANT_MANAGER
        )

        val addPayeeBalanceCommand = AddPayeeBalanceCommand(
            payeeId = payeeCreatedEvent.payeeId,
            amount = BigDecimal.TEN
        )

        val payeeBalanceAddedEvent = PayeeBalanceAddedEvent(
            payeeId = addPayeeBalanceCommand.payeeId,
            amount = addPayeeBalanceCommand.amount
        )

        testFixture.given(payeeCreatedEvent)
            .`when`(addPayeeBalanceCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(payeeBalanceAddedEvent)
    }

    @Test
    fun `given payee, should withdraw balance`() {
        val payeeCreatedEvent = PayeeCreatedEvent(
            payeeId = UUID.randomUUID(),
            userId = "userId",
            name = "name",
            email = "email",
            payeeType = PayeeCreatedEvent.PayeeType.RESTAURANT_MANAGER
        )

        val payeeBalanceAddedEvent = PayeeBalanceAddedEvent(
            payeeId = payeeCreatedEvent.payeeId,
            amount = BigDecimal.TEN
        )

        val withdrawPayeeBalanceCommand = WithdrawPayeeBalanceCommand(
            payeeId = payeeCreatedEvent.payeeId,
            userId = payeeCreatedEvent.userId,
            amount = BigDecimal.TEN,
            targetBankAccount = "targetBankAccount"
        )

        val payeeBalanceWithdrawnEvent = PayeeBalanceWithdrawnEvent(
            payeeId = withdrawPayeeBalanceCommand.payeeId,
            amount = withdrawPayeeBalanceCommand.amount,
            targetBankAccount = withdrawPayeeBalanceCommand.targetBankAccount,
            payeeName = payeeCreatedEvent.name,
            payeeEmail = payeeCreatedEvent.email,
            payeeType = PayeeBalanceWithdrawnEvent.PayeeType.RESTAURANT_MANAGER
        )

        testFixture.given(payeeCreatedEvent, payeeBalanceAddedEvent)
            .`when`(withdrawPayeeBalanceCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(payeeBalanceWithdrawnEvent)
    }

}
