package me.rasztabiga.thesis.saga.domain.command

import me.rasztabiga.thesis.saga.domain.command.saga.UserLifecycleSaga
import me.rasztabiga.thesis.shared.domain.command.command.CreatePayerCommand
import me.rasztabiga.thesis.shared.domain.command.event.PayerCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.UserCreatedEvent
import org.axonframework.test.saga.SagaTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class UserLifecycleSagaTest {

    private lateinit var testFixture: SagaTestFixture<UserLifecycleSaga>

    @BeforeEach
    fun setUp() {
        testFixture = SagaTestFixture(UserLifecycleSaga::class.java)
    }

    @Test
    fun `should execute saga`() {
        val userCreatedEvent = UserCreatedEvent(
            userId = UUID.randomUUID().toString(),
            name = "name",
            email = "email"
        )

        var payerId: UUID? = null

        testFixture.setCallbackBehavior { commandPayload, commandMetaData ->
            if (commandPayload is CreatePayerCommand) {
                payerId = commandPayload.id
            }
        }

        testFixture.whenPublishingA(userCreatedEvent)
            .expectDispatchedCommands(
                CreatePayerCommand(
                    id = payerId!!,
                    userId = userCreatedEvent.userId
                )
            )
            .expectActiveSagas(1)

        val payerCreatedEvent = PayerCreatedEvent(
            payerId!!,
            userCreatedEvent.userId
        )

        testFixture.whenPublishingA(payerCreatedEvent)
            .expectActiveSagas(0)
    }
}
