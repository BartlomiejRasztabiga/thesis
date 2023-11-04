package me.rasztabiga.thesis.saga.domain.command

import me.rasztabiga.thesis.saga.domain.command.saga.DeliveryCourierLifecycleSaga
import me.rasztabiga.thesis.shared.domain.command.command.CreatePayeeCommand
import me.rasztabiga.thesis.shared.domain.command.event.CourierCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.PayeeCreatedEvent
import org.axonframework.test.saga.SagaTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class DeliveryCourierLifecycleSagaTest {

    private lateinit var testFixture: SagaTestFixture<DeliveryCourierLifecycleSaga>

    @BeforeEach
    fun setUp() {
        testFixture = SagaTestFixture(DeliveryCourierLifecycleSaga::class.java)
    }

    @Test
    fun `should execute saga`() {
        val courierCreatedEvent = CourierCreatedEvent("courierId", "name", "email")

        var payeeId: UUID? = null

        testFixture.setCallbackBehavior { commandPayload, _ ->
            if (commandPayload is CreatePayeeCommand) {
                payeeId = commandPayload.id
            }
        }

        testFixture.whenPublishingA(courierCreatedEvent)
            .expectDispatchedCommands(
                CreatePayeeCommand(
                    id = payeeId!!,
                    userId = courierCreatedEvent.courierId,
                    name = courierCreatedEvent.name,
                    email = courierCreatedEvent.email,
                    payeeType = CreatePayeeCommand.PayeeType.COURIER
                )
            )
            .expectActiveSagas(1)
            .expectAssociationWith("payeeId", payeeId.toString())

        val payeeCreatedEvent = PayeeCreatedEvent(
            payeeId!!,
            courierCreatedEvent.courierId,
            courierCreatedEvent.name,
            courierCreatedEvent.email,
            PayeeCreatedEvent.PayeeType.COURIER
        )

        testFixture.whenPublishingA(payeeCreatedEvent)
            .expectActiveSagas(0)
    }
}
