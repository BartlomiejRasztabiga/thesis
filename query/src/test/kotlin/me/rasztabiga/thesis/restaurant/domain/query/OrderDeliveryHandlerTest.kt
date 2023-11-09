package me.rasztabiga.thesis.restaurant.domain.query

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import me.rasztabiga.thesis.query.domain.query.handler.OrderDeliveryHandler
import me.rasztabiga.thesis.query.domain.query.port.DistanceCalculatorPort
import me.rasztabiga.thesis.restaurant.domain.query.repository.InMemoryCourierRepository
import me.rasztabiga.thesis.restaurant.domain.query.repository.InMemoryOrderDeliveryRepository
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderDeliveryResponse
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryDeliveredEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryPickedUpEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryRejectedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindOrderDeliveryByIdQuery
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class OrderDeliveryHandlerTest {

    private lateinit var orderDeliveryHandler: OrderDeliveryHandler
    private lateinit var distanceCalculatorPort: DistanceCalculatorPort

    @BeforeEach
    fun setUp() {
        distanceCalculatorPort = mockk<DistanceCalculatorPort>()

        orderDeliveryHandler = OrderDeliveryHandler(
            InMemoryOrderDeliveryRepository(),
            InMemoryCourierRepository(),
            distanceCalculatorPort
        )
    }

    @Test
    fun `should handle OrderDeliveryCreatedEvent`() {
        val event = OrderDeliveryCreatedEvent(
            deliveryId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantLocation = Location(1.0, 1.0, null),
            deliveryLocation = Location(2.0, 2.0, null),
            courierFee = 1.toBigDecimal(),
        )

        orderDeliveryHandler.on(event)

        val savedOrderDelivery = orderDeliveryHandler.handle(FindOrderDeliveryByIdQuery(event.deliveryId)).block()
        savedOrderDelivery shouldNotBe null
        savedOrderDelivery?.id shouldBe event.deliveryId
        savedOrderDelivery?.restaurantLocation shouldBe event.restaurantLocation
        savedOrderDelivery?.deliveryLocation shouldBe event.deliveryLocation
        savedOrderDelivery?.status shouldBe OrderDeliveryResponse.DeliveryStatus.OFFER
        savedOrderDelivery?.courierFee shouldBe event.courierFee
        savedOrderDelivery?.createdAt shouldNotBe null
    }

    @Test
    fun `should handle OrderDeliveryRejectedEvent`() {
        val orderDeliveryCreatedEvent = OrderDeliveryCreatedEvent(
            deliveryId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantLocation = Location(1.0, 1.0, null),
            deliveryLocation = Location(2.0, 2.0, null),
            courierFee = 1.toBigDecimal(),
        )

        val orderDeliveryRejectedEvent = OrderDeliveryRejectedEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            courierId = UUID.randomUUID().toString(),
            restaurantLocation = orderDeliveryCreatedEvent.restaurantLocation,
        )

        orderDeliveryHandler.on(orderDeliveryCreatedEvent)
        orderDeliveryHandler.on(orderDeliveryRejectedEvent)

        val savedOrderDelivery =
            orderDeliveryHandler.handle(FindOrderDeliveryByIdQuery(orderDeliveryRejectedEvent.deliveryId)).block()
        savedOrderDelivery shouldNotBe null
        savedOrderDelivery?.id shouldBe orderDeliveryCreatedEvent.deliveryId
        savedOrderDelivery?.restaurantLocation shouldBe orderDeliveryCreatedEvent.restaurantLocation
        savedOrderDelivery?.deliveryLocation shouldBe orderDeliveryCreatedEvent.deliveryLocation
        savedOrderDelivery?.status shouldBe OrderDeliveryResponse.DeliveryStatus.OFFER
        savedOrderDelivery?.courierFee shouldBe orderDeliveryCreatedEvent.courierFee
        savedOrderDelivery?.createdAt shouldNotBe null
    }

    @Test
    fun `should handle OrderDeliveryAcceptedEvent`() {
        val orderDeliveryCreatedEvent = OrderDeliveryCreatedEvent(
            deliveryId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantLocation = Location(1.0, 1.0, null),
            deliveryLocation = Location(2.0, 2.0, null),
            courierFee = 1.toBigDecimal(),
        )

        val orderDeliveryAcceptedEvent = OrderDeliveryAcceptedEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            orderId = orderDeliveryCreatedEvent.orderId,
            courierId = UUID.randomUUID().toString()
        )

        orderDeliveryHandler.on(orderDeliveryCreatedEvent)
        orderDeliveryHandler.on(orderDeliveryAcceptedEvent)

        val savedOrderDelivery =
            orderDeliveryHandler.handle(FindOrderDeliveryByIdQuery(orderDeliveryAcceptedEvent.deliveryId)).block()
        savedOrderDelivery shouldNotBe null
        savedOrderDelivery?.id shouldBe orderDeliveryCreatedEvent.deliveryId
        savedOrderDelivery?.restaurantLocation shouldBe orderDeliveryCreatedEvent.restaurantLocation
        savedOrderDelivery?.deliveryLocation shouldBe orderDeliveryCreatedEvent.deliveryLocation
        savedOrderDelivery?.status shouldBe OrderDeliveryResponse.DeliveryStatus.ACCEPTED
        savedOrderDelivery?.courierFee shouldBe orderDeliveryCreatedEvent.courierFee
        savedOrderDelivery?.createdAt shouldNotBe null
    }

    @Test
    fun `should handle OrderDeliveryPickedUpEvent`() {
        val orderDeliveryCreatedEvent = OrderDeliveryCreatedEvent(
            deliveryId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantLocation = Location(1.0, 1.0, null),
            deliveryLocation = Location(2.0, 2.0, null),
            courierFee = 1.toBigDecimal(),
        )

        val orderDeliveryAcceptedEvent = OrderDeliveryAcceptedEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            orderId = orderDeliveryCreatedEvent.orderId,
            courierId = UUID.randomUUID().toString()
        )

        val orderDeliveryPickedUpEvent = OrderDeliveryPickedUpEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            orderId = orderDeliveryCreatedEvent.orderId,
            courierId = UUID.randomUUID().toString()
        )

        orderDeliveryHandler.on(orderDeliveryCreatedEvent)
        orderDeliveryHandler.on(orderDeliveryAcceptedEvent)
        orderDeliveryHandler.on(orderDeliveryPickedUpEvent)

        val savedOrderDelivery =
            orderDeliveryHandler.handle(FindOrderDeliveryByIdQuery(orderDeliveryAcceptedEvent.deliveryId)).block()
        savedOrderDelivery shouldNotBe null
        savedOrderDelivery?.id shouldBe orderDeliveryCreatedEvent.deliveryId
        savedOrderDelivery?.restaurantLocation shouldBe orderDeliveryCreatedEvent.restaurantLocation
        savedOrderDelivery?.deliveryLocation shouldBe orderDeliveryCreatedEvent.deliveryLocation
        savedOrderDelivery?.status shouldBe OrderDeliveryResponse.DeliveryStatus.PICKED_UP
        savedOrderDelivery?.courierFee shouldBe orderDeliveryCreatedEvent.courierFee
        savedOrderDelivery?.createdAt shouldNotBe null
    }

    @Test
    fun `should handle OrderDeliveryDeliveredEvent`() {
        val orderDeliveryCreatedEvent = OrderDeliveryCreatedEvent(
            deliveryId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantLocation = Location(1.0, 1.0, null),
            deliveryLocation = Location(2.0, 2.0, null),
            courierFee = 1.toBigDecimal(),
        )

        val orderDeliveryAcceptedEvent = OrderDeliveryAcceptedEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            orderId = orderDeliveryCreatedEvent.orderId,
            courierId = UUID.randomUUID().toString()
        )

        val orderDeliveryPickedUpEvent = OrderDeliveryPickedUpEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            orderId = orderDeliveryCreatedEvent.orderId,
            courierId = UUID.randomUUID().toString()
        )

        val orderDeliveryDeliveredEvent = OrderDeliveryDeliveredEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            orderId = orderDeliveryCreatedEvent.orderId,
            courierId = UUID.randomUUID().toString(),
            courierFee = orderDeliveryCreatedEvent.courierFee
        )

        orderDeliveryHandler.on(orderDeliveryCreatedEvent)
        orderDeliveryHandler.on(orderDeliveryAcceptedEvent)
        orderDeliveryHandler.on(orderDeliveryPickedUpEvent)
        orderDeliveryHandler.on(orderDeliveryDeliveredEvent)

        val savedOrderDelivery =
            orderDeliveryHandler.handle(FindOrderDeliveryByIdQuery(orderDeliveryAcceptedEvent.deliveryId)).block()
        savedOrderDelivery shouldNotBe null
        savedOrderDelivery?.id shouldBe orderDeliveryCreatedEvent.deliveryId
        savedOrderDelivery?.restaurantLocation shouldBe orderDeliveryCreatedEvent.restaurantLocation
        savedOrderDelivery?.deliveryLocation shouldBe orderDeliveryCreatedEvent.deliveryLocation
        savedOrderDelivery?.status shouldBe OrderDeliveryResponse.DeliveryStatus.DELIVERED
        savedOrderDelivery?.courierFee shouldBe orderDeliveryCreatedEvent.courierFee
        savedOrderDelivery?.createdAt shouldNotBe null
    }
}
