package me.rasztabiga.thesis.order.domain.command

import me.rasztabiga.thesis.order.domain.command.aggregate.User
import me.rasztabiga.thesis.order.domain.command.command.CreateDeliveryAddressCommand
import me.rasztabiga.thesis.order.domain.command.command.CreateUserCommand
import me.rasztabiga.thesis.order.domain.command.command.DeleteDeliveryAddressCommand
import me.rasztabiga.thesis.order.domain.command.event.DeliveryAddressCreatedEvent
import me.rasztabiga.thesis.order.domain.command.event.DeliveryAddressDeletedEvent
import me.rasztabiga.thesis.shared.domain.command.event.UserCreatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class UserTest {

    private lateinit var testFixture: AggregateTestFixture<User>

    @BeforeEach
    fun setUp() {
        testFixture = AggregateTestFixture(User::class.java)
    }

    @Test
    fun `should create user`() {
        val createUserCommand = CreateUserCommand(
            "1",
            "User"
        )

        val userCreatedEvent = UserCreatedEvent(
            createUserCommand.id,
            createUserCommand.name
        )

        testFixture.givenNoPriorActivity()
            .`when`(createUserCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(userCreatedEvent)
    }

    @Test
    fun `should add delivery address`() {
        val userCreatedEvent = UserCreatedEvent(
            "1",
            "name"
        )

        val createDeliveryAddressCommand = CreateDeliveryAddressCommand(
            userCreatedEvent.id,
            UUID.randomUUID(),
            "Address",
            "Additional info"
        )

        val deliveryAddressCreatedEvent = DeliveryAddressCreatedEvent(
            userCreatedEvent.id,
            createDeliveryAddressCommand.addressId,
            createDeliveryAddressCommand.address,
            createDeliveryAddressCommand.additionalInfo
        )

        testFixture.given(userCreatedEvent)
            .`when`(createDeliveryAddressCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(deliveryAddressCreatedEvent)
    }

    @Test
    fun `should delete delivery address`() {
        val userCreatedEvent = UserCreatedEvent(
            "1",
            "name"
        )

        val deliveryAddressCreatedEvent = DeliveryAddressCreatedEvent(
            userCreatedEvent.id,
            UUID.randomUUID(),
            "address",
            "additional info"
        )

        val deleteDeliveryAddressCommand = DeleteDeliveryAddressCommand(
            userCreatedEvent.id,
            deliveryAddressCreatedEvent.addressId
        )

        val deliveryAddressDeletedEvent = DeliveryAddressDeletedEvent(
            userCreatedEvent.id,
            deliveryAddressCreatedEvent.addressId
        )

        testFixture.given(userCreatedEvent, deliveryAddressCreatedEvent)
            .`when`(deleteDeliveryAddressCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(deliveryAddressDeletedEvent)
    }
}
