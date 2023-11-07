package me.rasztabiga.thesis.restaurant.domain.query

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import me.rasztabiga.thesis.query.domain.query.handler.CourierHandler
import me.rasztabiga.thesis.query.domain.query.port.DistanceCalculatorPort
import me.rasztabiga.thesis.restaurant.domain.query.repository.InMemoryCourierRepository
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CourierResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import me.rasztabiga.thesis.shared.domain.command.event.CourierAvailabilityUpdatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.CourierCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.CourierLocationUpdatedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindCourierByIdQuery
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CourierHandlerTest {

    private lateinit var courierHandler: CourierHandler
    private lateinit var distanceCalculatorPort: DistanceCalculatorPort

    @BeforeEach
    fun setUp() {
        distanceCalculatorPort = mockk<DistanceCalculatorPort>()
        courierHandler = CourierHandler(InMemoryCourierRepository(), distanceCalculatorPort)
    }

    @Test
    fun `should handle CourierCreatedEvent`() {
        val event = CourierCreatedEvent(
            courierId = UUID.randomUUID().toString(),
            name = "name",
            email = "email"
        )

        courierHandler.on(event)

        val savedCourier = courierHandler.handle(FindCourierByIdQuery(event.courierId)).block()
        savedCourier shouldNotBe null
        savedCourier?.id shouldBe event.courierId
        savedCourier?.name shouldBe event.name
        savedCourier?.email shouldBe event.email
        savedCourier?.availability shouldBe CourierResponse.Availability.OFFLINE
        savedCourier?.location shouldBe null
    }

    @Test
    fun `should handle CourierAvailabilityUpdatedEvent`() {
        val courierCreatedEvent = CourierCreatedEvent(
            courierId = UUID.randomUUID().toString(),
            name = "name",
            email = "email"
        )

        val event = CourierAvailabilityUpdatedEvent(
            id = courierCreatedEvent.courierId,
            availability = CourierAvailabilityUpdatedEvent.Availability.ONLINE
        )

        courierHandler.on(courierCreatedEvent)
        courierHandler.on(event)

        val savedCourier = courierHandler.handle(FindCourierByIdQuery(courierCreatedEvent.courierId)).block()
        savedCourier shouldNotBe null
        savedCourier?.id shouldBe courierCreatedEvent.courierId
        savedCourier?.name shouldBe courierCreatedEvent.name
        savedCourier?.email shouldBe courierCreatedEvent.email
        savedCourier?.availability shouldBe CourierResponse.Availability.ONLINE
        savedCourier?.location shouldBe null
    }

    @Test
    fun `should handle CourierLocationUpdatedEvent`() {
        val courierCreatedEvent = CourierCreatedEvent(
            courierId = UUID.randomUUID().toString(),
            name = "name",
            email = "email"
        )

        val event = CourierLocationUpdatedEvent(
            id = courierCreatedEvent.courierId,
            location = Location(1.0, 2.0, null)
        )

        courierHandler.on(courierCreatedEvent)
        courierHandler.on(event)

        val savedCourier = courierHandler.handle(FindCourierByIdQuery(courierCreatedEvent.courierId)).block()
        savedCourier shouldNotBe null
        savedCourier?.id shouldBe courierCreatedEvent.courierId
        savedCourier?.name shouldBe courierCreatedEvent.name
        savedCourier?.email shouldBe courierCreatedEvent.email
        savedCourier?.availability shouldBe CourierResponse.Availability.OFFLINE
        savedCourier?.location shouldBe event.location
    }
}
