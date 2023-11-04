package me.rasztabiga.thesis.saga.domain.command

import me.rasztabiga.thesis.saga.domain.command.saga.RestaurantLifecycleSaga
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import me.rasztabiga.thesis.shared.domain.command.command.CreatePayeeCommand
import me.rasztabiga.thesis.shared.domain.command.event.PayeeCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantCreatedEvent
import org.axonframework.test.saga.SagaTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RestaurantLifecycleSagaTest {

    private lateinit var testFixture: SagaTestFixture<RestaurantLifecycleSaga>

    @BeforeEach
    fun setUp() {
        testFixture = SagaTestFixture(RestaurantLifecycleSaga::class.java)
    }

    @Test
    fun `should execute saga`() {
        val restaurantCreatedEvent = RestaurantCreatedEvent(
            restaurantId = UUID.randomUUID(),
            name = "name",
            location = Location(0.0, 0.0, null),
            managerId = UUID.randomUUID().toString(),
            email = "email",
            imageUrl = "imageUrl"
        )

        var payeeId: UUID? = null

        testFixture.setCallbackBehavior { commandPayload, commandMetaData ->
            if (commandPayload is CreatePayeeCommand) {
                payeeId = commandPayload.id
            }
        }

        testFixture.whenPublishingA(restaurantCreatedEvent)
            .expectDispatchedCommands(
                CreatePayeeCommand(
                    id = payeeId!!,
                    userId = restaurantCreatedEvent.managerId,
                    name = restaurantCreatedEvent.name,
                    email = restaurantCreatedEvent.email,
                    payeeType = CreatePayeeCommand.PayeeType.RESTAURANT_MANAGER
                )
            )
            .expectActiveSagas(1)
            .expectAssociationWith("payeeId", payeeId.toString())

        val payeeCreatedEvent = PayeeCreatedEvent(
            payeeId!!,
            restaurantCreatedEvent.managerId,
            restaurantCreatedEvent.name,
            restaurantCreatedEvent.email,
            PayeeCreatedEvent.PayeeType.RESTAURANT_MANAGER
        )

        testFixture.whenPublishingA(payeeCreatedEvent)
            .expectActiveSagas(0)
    }
}
