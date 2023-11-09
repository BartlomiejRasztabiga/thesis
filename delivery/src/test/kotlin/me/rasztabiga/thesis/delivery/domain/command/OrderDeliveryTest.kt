package me.rasztabiga.thesis.delivery.domain.command

import io.mockk.every
import io.mockk.mockk
import me.rasztabiga.thesis.delivery.domain.command.aggregate.OrderDelivery
import me.rasztabiga.thesis.delivery.domain.command.command.AcceptDeliveryOfferCommand
import me.rasztabiga.thesis.delivery.domain.command.command.DeliverDeliveryCommand
import me.rasztabiga.thesis.delivery.domain.command.command.PickupDeliveryCommand
import me.rasztabiga.thesis.delivery.domain.command.command.RejectDeliveryOfferCommand
import me.rasztabiga.thesis.delivery.domain.command.port.CalculateDeliveryFeePort
import me.rasztabiga.thesis.delivery.domain.command.port.CourierOnlineVerifierPort
import me.rasztabiga.thesis.delivery.domain.command.port.OrderPreparedVerifierPort
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderDeliveryOfferCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryAssignedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryDeliveredEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryPickedUpEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryRejectedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class OrderDeliveryTest {

    private lateinit var testFixture: AggregateTestFixture<OrderDelivery>
    private lateinit var calculateDeliveryFeePort: CalculateDeliveryFeePort
    private lateinit var courierOnlineVerifierPort: CourierOnlineVerifierPort
    private lateinit var orderPreparedVerifierPort: OrderPreparedVerifierPort

    @BeforeEach
    fun setUp() {
        calculateDeliveryFeePort = mockk<CalculateDeliveryFeePort>()
        courierOnlineVerifierPort = mockk<CourierOnlineVerifierPort>()
        orderPreparedVerifierPort = mockk<OrderPreparedVerifierPort>()

        testFixture = AggregateTestFixture(OrderDelivery::class.java)
        testFixture.registerInjectableResource(calculateDeliveryFeePort)
        testFixture.registerInjectableResource(courierOnlineVerifierPort)
        testFixture.registerInjectableResource(orderPreparedVerifierPort)
    }

    @Test
    fun `should create order delivery offer`() {
        every { calculateDeliveryFeePort.calculateBaseFee(any(), any()) } returns BigDecimal(10)

        val createOrderDeliveryCommand = CreateOrderDeliveryOfferCommand(
            id = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantLocation = Location(lat = 1.0, lng = 1.0, streetAddress = "street"),
            deliveryLocation = Location(lat = 1.0, lng = 1.0, streetAddress = "street")
        )

        val orderDeliveryCreatedEvent = OrderDeliveryCreatedEvent(
            deliveryId = createOrderDeliveryCommand.id,
            orderId = createOrderDeliveryCommand.orderId,
            restaurantLocation = createOrderDeliveryCommand.restaurantLocation,
            deliveryLocation = createOrderDeliveryCommand.deliveryLocation,
            courierFee = BigDecimal(10)
        )

        testFixture.givenNoPriorActivity()
            .`when`(createOrderDeliveryCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderDeliveryCreatedEvent)
    }

    @Test
    fun `given OFFER and online courier, should reject delivery offer`() {
        val orderDeliveryCreatedEvent = OrderDeliveryCreatedEvent(
            deliveryId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantLocation = Location(lat = 1.0, lng = 1.0, streetAddress = "street"),
            deliveryLocation = Location(lat = 1.0, lng = 1.0, streetAddress = "street"),
            courierFee = BigDecimal(10)
        )

        val courierId = UUID.randomUUID().toString()

        val orderDeliveryAssignedEvent = OrderDeliveryAssignedEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            courierId = courierId
        )

        every { courierOnlineVerifierPort.isCourierOnline(courierId) } returns true

        val rejectDeliveryOfferCommand = RejectDeliveryOfferCommand(
            id = orderDeliveryCreatedEvent.deliveryId,
            courierId = courierId
        )

        val orderDeliveryRejectedEvent = OrderDeliveryRejectedEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            courierId = courierId,
            restaurantLocation = orderDeliveryCreatedEvent.restaurantLocation
        )

        testFixture.given(orderDeliveryCreatedEvent, orderDeliveryAssignedEvent)
            .`when`(rejectDeliveryOfferCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderDeliveryRejectedEvent)
    }

    @Test
    fun `given OFFER and online courier, should accept delivery offer`() {
        val orderDeliveryCreatedEvent = OrderDeliveryCreatedEvent(
            deliveryId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantLocation = Location(lat = 1.0, lng = 1.0, streetAddress = "street"),
            deliveryLocation = Location(lat = 1.0, lng = 1.0, streetAddress = "street"),
            courierFee = BigDecimal(10)
        )

        val courierId = UUID.randomUUID().toString()

        val orderDeliveryAssignedEvent = OrderDeliveryAssignedEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            courierId = courierId
        )

        every { courierOnlineVerifierPort.isCourierOnline(courierId) } returns true

        val acceptDeliveryOfferCommand = AcceptDeliveryOfferCommand(
            id = orderDeliveryCreatedEvent.deliveryId,
            courierId = courierId
        )

        val orderDeliveryAcceptedEvent = OrderDeliveryAcceptedEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            orderId = orderDeliveryCreatedEvent.orderId,
            courierId = courierId
        )

        testFixture.given(orderDeliveryCreatedEvent, orderDeliveryAssignedEvent)
            .`when`(acceptDeliveryOfferCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderDeliveryAcceptedEvent)
    }

    @Test
    fun `given ACCEPTED delivery, online courier and prepared order, should pickup delivery`() {
        val orderDeliveryCreatedEvent = OrderDeliveryCreatedEvent(
            deliveryId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantLocation = Location(lat = 1.0, lng = 1.0, streetAddress = "street"),
            deliveryLocation = Location(lat = 1.0, lng = 1.0, streetAddress = "street"),
            courierFee = BigDecimal(10)
        )

        val orderDeliveryAssignedEvent = OrderDeliveryAssignedEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            courierId = UUID.randomUUID().toString()
        )

        val orderDeliveryAcceptedEvent = OrderDeliveryAcceptedEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            orderId = orderDeliveryCreatedEvent.orderId,
            courierId = orderDeliveryAssignedEvent.courierId
        )

        every { courierOnlineVerifierPort.isCourierOnline(orderDeliveryAcceptedEvent.courierId) } returns true
        every { orderPreparedVerifierPort.isOrderPrepared(orderDeliveryAcceptedEvent.orderId) } returns true

        val pickupDeliveryCommand = PickupDeliveryCommand(
            id = orderDeliveryCreatedEvent.deliveryId,
            courierId = orderDeliveryAcceptedEvent.courierId
        )

        val orderDeliveryPickedUpEvent = OrderDeliveryPickedUpEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            courierId = orderDeliveryAcceptedEvent.courierId,
            orderId = orderDeliveryCreatedEvent.orderId
        )

        testFixture.given(orderDeliveryCreatedEvent, orderDeliveryAssignedEvent, orderDeliveryAcceptedEvent)
            .`when`(pickupDeliveryCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderDeliveryPickedUpEvent)
    }

    @Test
    fun `given PICKED_UP delivery and online courier, should deliver delivery`() {
        val orderDeliveryCreatedEvent = OrderDeliveryCreatedEvent(
            deliveryId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantLocation = Location(lat = 1.0, lng = 1.0, streetAddress = "street"),
            deliveryLocation = Location(lat = 1.0, lng = 1.0, streetAddress = "street"),
            courierFee = BigDecimal(10)
        )

        val orderDeliveryAssignedEvent = OrderDeliveryAssignedEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            courierId = UUID.randomUUID().toString()
        )

        val orderDeliveryAcceptedEvent = OrderDeliveryAcceptedEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            orderId = orderDeliveryCreatedEvent.orderId,
            courierId = orderDeliveryAssignedEvent.courierId
        )

        val orderDeliveryPickedUpEvent = OrderDeliveryPickedUpEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            courierId = orderDeliveryAcceptedEvent.courierId,
            orderId = orderDeliveryCreatedEvent.orderId
        )

        every { courierOnlineVerifierPort.isCourierOnline(orderDeliveryAcceptedEvent.courierId) } returns true

        val deliverDeliveryCommand = DeliverDeliveryCommand(
            id = orderDeliveryCreatedEvent.deliveryId,
            courierId = orderDeliveryAcceptedEvent.courierId
        )

        val orderDeliveryDeliveredEvent = OrderDeliveryDeliveredEvent(
            deliveryId = orderDeliveryCreatedEvent.deliveryId,
            courierId = orderDeliveryAcceptedEvent.courierId,
            orderId = orderDeliveryCreatedEvent.orderId,
            courierFee = orderDeliveryCreatedEvent.courierFee
        )

        testFixture.given(
            orderDeliveryCreatedEvent,
            orderDeliveryAssignedEvent,
            orderDeliveryAcceptedEvent,
            orderDeliveryPickedUpEvent
        )
            .`when`(deliverDeliveryCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderDeliveryDeliveredEvent)
    }
}
