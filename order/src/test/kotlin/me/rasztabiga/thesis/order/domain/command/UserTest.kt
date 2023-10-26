package me.rasztabiga.thesis.order.domain.command

import io.mockk.every
import io.mockk.mockk
import me.rasztabiga.thesis.order.domain.command.aggregate.User
import me.rasztabiga.thesis.order.domain.command.command.CreateDeliveryAddressCommand
import me.rasztabiga.thesis.order.domain.command.command.CreateUserCommand
import me.rasztabiga.thesis.order.domain.command.command.DeleteDeliveryAddressCommand
import me.rasztabiga.thesis.order.domain.command.command.UpdateDefaultDeliveryAddressCommand
import me.rasztabiga.thesis.order.domain.command.port.GeocodeAddressPort
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import me.rasztabiga.thesis.shared.domain.command.event.DefaultDeliveryAddressUpdatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.DeliveryAddressCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.DeliveryAddressDeletedEvent
import me.rasztabiga.thesis.shared.domain.command.event.UserCreatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class UserTest {

    private lateinit var testFixture: AggregateTestFixture<User>
    private lateinit var geocodeAddressPort: GeocodeAddressPort

    @BeforeEach
    fun setUp() {
        geocodeAddressPort = mockk<GeocodeAddressPort>()

        testFixture = AggregateTestFixture(User::class.java)
        testFixture.registerInjectableResource(geocodeAddressPort)
    }

    @Test
    fun `should create user`() {
        val createUserCommand = CreateUserCommand(
            id = UUID.randomUUID().toString(),
            name = "John Doe",
            email = "john@example.com"
        )

        val userCreatedEvent = UserCreatedEvent(
            userId = createUserCommand.id,
            name = createUserCommand.name,
            email = createUserCommand.email
        )

        testFixture.givenNoPriorActivity()
            .`when`(createUserCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(userCreatedEvent)
    }

    @Test
    fun `given user, should create delivery address`() {
        val userCreatedEvent = UserCreatedEvent(
            userId = UUID.randomUUID().toString(),
            name = "John Doe",
            email = "john@example.com"
        )

        val createDeliveryAddressCommand = CreateDeliveryAddressCommand(
            userId = userCreatedEvent.userId,
            addressId = UUID.randomUUID(),
            address = "Example Street 1/2, 00-000 City",
            additionalInfo = null
        )

        val location = Location(
            lat = 52.0,
            lng = 21.0,
            streetAddress = createDeliveryAddressCommand.address
        )

        every { geocodeAddressPort.geocode(createDeliveryAddressCommand.address) } returns location

        val deliveryAddressCreatedEvent = DeliveryAddressCreatedEvent(
            userId = createDeliveryAddressCommand.userId,
            addressId = createDeliveryAddressCommand.addressId,
            location = location
        )

        testFixture.given(userCreatedEvent)
            .`when`(createDeliveryAddressCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(deliveryAddressCreatedEvent)
    }

    @Test
    fun `given user with delivery address, should remove address`() {
        val userCreatedEvent = UserCreatedEvent(
            userId = UUID.randomUUID().toString(),
            name = "John Doe",
            email = "john@example.com"
        )

        val deliveryAddressCreatedEvent = DeliveryAddressCreatedEvent(
            userId = userCreatedEvent.userId,
            addressId = UUID.randomUUID(),
            location = Location(
                lat = 52.0,
                lng = 21.0,
                streetAddress = "Example Street 1/2, 00-000 City"
            )
        )

        val deleteDeliveryAddressCommand = DeleteDeliveryAddressCommand(
            userId = userCreatedEvent.userId,
            addressId = deliveryAddressCreatedEvent.addressId
        )

        val deliveryAddressDeletedEvent = DeliveryAddressDeletedEvent(
            userId = deleteDeliveryAddressCommand.userId,
            addressId = deleteDeliveryAddressCommand.addressId
        )

        testFixture.given(userCreatedEvent, deliveryAddressCreatedEvent)
            .`when`(deleteDeliveryAddressCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(deliveryAddressDeletedEvent)
    }

    @Test
    fun `given user with delivery address, should update default address`() {
        val userCreatedEvent = UserCreatedEvent(
            userId = UUID.randomUUID().toString(),
            name = "John Doe",
            email = "john@example.com"
        )

        val deliveryAddressCreatedEvent = DeliveryAddressCreatedEvent(
            userId = userCreatedEvent.userId,
            addressId = UUID.randomUUID(),
            location = Location(
                lat = 52.0,
                lng = 21.0,
                streetAddress = "Example Street 1/2, 00-000 City"
            )
        )

        val updateDefaultDeliveryAddressCommand = UpdateDefaultDeliveryAddressCommand(
            userId = userCreatedEvent.userId,
            addressId = deliveryAddressCreatedEvent.addressId
        )

        val defaultDeliveryAddressUpdatedEvent = DefaultDeliveryAddressUpdatedEvent(
            userId = updateDefaultDeliveryAddressCommand.userId,
            addressId = updateDefaultDeliveryAddressCommand.addressId
        )

        testFixture.given(userCreatedEvent, deliveryAddressCreatedEvent)
            .`when`(updateDefaultDeliveryAddressCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(defaultDeliveryAddressUpdatedEvent)
    }
}
