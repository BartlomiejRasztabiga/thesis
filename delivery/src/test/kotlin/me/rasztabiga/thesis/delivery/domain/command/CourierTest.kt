package me.rasztabiga.thesis.delivery.domain.command

import me.rasztabiga.thesis.delivery.domain.command.aggregate.Availability
import me.rasztabiga.thesis.delivery.domain.command.aggregate.Courier
import me.rasztabiga.thesis.delivery.domain.command.command.CreateCourierCommand
import me.rasztabiga.thesis.delivery.domain.command.command.UpdateCourierAvailabilityCommand
import me.rasztabiga.thesis.delivery.domain.command.command.UpdateCourierLocationCommand
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import me.rasztabiga.thesis.shared.domain.command.event.CourierAvailabilityUpdatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.CourierCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.CourierLocationUpdatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CourierTest {

    private lateinit var testFixture: AggregateTestFixture<Courier>

    @BeforeEach
    fun setUp() {
        testFixture = AggregateTestFixture(Courier::class.java)
    }

    @Test
    fun `should create courier`() {
        val createCourierCommand = CreateCourierCommand(
            id = UUID.randomUUID().toString(),
            name = "John",
            email = "john@example.com"
        )

        val courierCreatedEvent = CourierCreatedEvent(
            courierId = createCourierCommand.id,
            name = createCourierCommand.name,
            email = createCourierCommand.email
        )

        testFixture.givenNoPriorActivity()
            .`when`(createCourierCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(courierCreatedEvent)
    }

    @Test
    fun `should update courier availability`() {
        val courierCreatedEvent = CourierCreatedEvent(
            courierId = UUID.randomUUID().toString(),
            name = "John",
            email = "john@example.com"
        )

        val updateCourierAvailabilityCommand = UpdateCourierAvailabilityCommand(
            id = courierCreatedEvent.courierId,
            availability = Availability.ONLINE
        )

        val courierAvailabilityUpdatedEvent = CourierAvailabilityUpdatedEvent(
            id = courierCreatedEvent.courierId,
            availability = CourierAvailabilityUpdatedEvent.Availability.ONLINE
        )

        testFixture.given(courierCreatedEvent)
            .`when`(updateCourierAvailabilityCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(courierAvailabilityUpdatedEvent)
    }

    @Test
    fun `should update courier location`() {
        val courierCreatedEvent = CourierCreatedEvent(
            courierId = UUID.randomUUID().toString(),
            name = "John",
            email = "john@example.com"
        )

        val updateCourierLocationCommand = UpdateCourierLocationCommand(
            id = courierCreatedEvent.courierId,
            location = Location(1.0, 1.0, null)
        )

        val courierLocationUpdatedEvent = CourierLocationUpdatedEvent(
            id = courierCreatedEvent.courierId,
            location = Location(1.0, 1.0, null)
        )

        testFixture.given(courierCreatedEvent)
            .`when`(updateCourierLocationCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(courierLocationUpdatedEvent)
    }


}
