package me.rasztabiga.thesis.order.domain.command

import io.mockk.every
import io.mockk.mockk
import me.rasztabiga.thesis.order.domain.command.aggregate.Order
import me.rasztabiga.thesis.order.domain.command.command.AddOrderItemCommand
import me.rasztabiga.thesis.order.domain.command.command.CancelOrderCommand
import me.rasztabiga.thesis.order.domain.command.command.DeleteOrderItemCommand
import me.rasztabiga.thesis.order.domain.command.command.FinalizeOrderCommand
import me.rasztabiga.thesis.order.domain.command.command.RateOrderCommand
import me.rasztabiga.thesis.order.domain.command.command.StartOrderCommand
import me.rasztabiga.thesis.order.domain.command.port.OrderVerificationPort
import me.rasztabiga.thesis.shared.domain.command.command.MarkOrderAsDeliveredCommand
import me.rasztabiga.thesis.shared.domain.command.command.MarkOrderAsPaidCommand
import me.rasztabiga.thesis.shared.domain.command.command.RejectOrderCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderCanceledEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveredEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderFinalizedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderItemAddedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderItemDeletedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaidEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderRatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderRejectedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderStartedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class OrderTest {

    private lateinit var testFixture: AggregateTestFixture<Order>
    private lateinit var orderVerificationPort: OrderVerificationPort

    @BeforeEach
    fun setUp() {
        orderVerificationPort = mockk<OrderVerificationPort>()

        testFixture = AggregateTestFixture(Order::class.java)
        testFixture.registerInjectableResource(orderVerificationPort)
    }

    @Test
    fun `given open restaurant and user, should start order`() {
        val startOrderCommand = StartOrderCommand(
            orderId = UUID.randomUUID(),
            userId = "user",
            restaurantId = UUID.randomUUID()
        )

        every { orderVerificationPort.restaurantExists(startOrderCommand.restaurantId) } returns true
        every { orderVerificationPort.isRestaurantOpen(startOrderCommand.restaurantId) } returns true
        every { orderVerificationPort.userExists(startOrderCommand.userId) } returns true

        val orderStartedEvent = OrderStartedEvent(
            orderId = startOrderCommand.orderId,
            userId = startOrderCommand.userId,
            restaurantId = startOrderCommand.restaurantId,
            status = OrderStartedEvent.OrderStatus.CREATED
        )

        testFixture.givenNoPriorActivity()
            .`when`(startOrderCommand)
            .expectEvents(orderStartedEvent)
    }

    @Test
    fun `given created order, should cancel order`() {
        val orderStartedEvent = OrderStartedEvent(
            orderId = UUID.randomUUID(),
            userId = "user",
            restaurantId = UUID.randomUUID(),
            status = OrderStartedEvent.OrderStatus.CREATED
        )

        val cancelOrderCommand = CancelOrderCommand(
            orderId = orderStartedEvent.orderId,
            userId = orderStartedEvent.userId
        )

        val orderCanceledEvent = OrderCanceledEvent(
            orderId = orderStartedEvent.orderId
        )

        testFixture.given(orderStartedEvent)
            .`when`(cancelOrderCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderCanceledEvent)
    }

    @Test
    fun `given created order, should add item`() {
        val orderStartedEvent = OrderStartedEvent(
            orderId = UUID.randomUUID(),
            userId = "user",
            restaurantId = UUID.randomUUID(),
            status = OrderStartedEvent.OrderStatus.CREATED
        )

        val addOrderItemCommand = AddOrderItemCommand(
            orderId = orderStartedEvent.orderId,
            userId = orderStartedEvent.userId,
            productId = UUID.randomUUID()
        )

        val orderItemAddedEvent = OrderItemAddedEvent(
            orderId = orderStartedEvent.orderId,
            productId = addOrderItemCommand.productId
        )

        testFixture.given(orderStartedEvent)
            .`when`(addOrderItemCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderItemAddedEvent)
    }

    @Test
    fun `given created order with one item, should remove item`() {
        val orderStartedEvent = OrderStartedEvent(
            orderId = UUID.randomUUID(),
            userId = "user",
            restaurantId = UUID.randomUUID(),
            status = OrderStartedEvent.OrderStatus.CREATED
        )

        val addOrderItemCommand = AddOrderItemCommand(
            orderId = orderStartedEvent.orderId,
            userId = orderStartedEvent.userId,
            productId = UUID.randomUUID()
        )

        val orderItemAddedEvent = OrderItemAddedEvent(
            orderId = orderStartedEvent.orderId,
            productId = addOrderItemCommand.productId
        )

        val deleteOrderItemCommand = DeleteOrderItemCommand(
            orderId = orderStartedEvent.orderId,
            userId = orderStartedEvent.userId,
            productId = addOrderItemCommand.productId
        )

        val orderItemDeletedEvent = OrderItemDeletedEvent(
            orderId = orderStartedEvent.orderId,
            productId = addOrderItemCommand.productId
        )

        testFixture.given(orderStartedEvent, orderItemAddedEvent)
            .`when`(deleteOrderItemCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderItemDeletedEvent)
    }

    @Test
    fun `given created order, should finalize order`() {
        val orderStartedEvent = OrderStartedEvent(
            orderId = UUID.randomUUID(),
            userId = "user",
            restaurantId = UUID.randomUUID(),
            status = OrderStartedEvent.OrderStatus.CREATED
        )

        val orderItemAddedEvent = OrderItemAddedEvent(
            orderId = orderStartedEvent.orderId,
            productId = UUID.randomUUID()
        )

        val finalizeOrderCommand = FinalizeOrderCommand(
            orderId = orderStartedEvent.orderId,
            userId = orderStartedEvent.userId
        )

        every {
            orderVerificationPort.productExists(
                orderItemAddedEvent.productId,
                orderStartedEvent.restaurantId
            )
        } returns true

        val orderFinalizedEvent = OrderFinalizedEvent(
            orderId = orderStartedEvent.orderId,
            userId = orderStartedEvent.userId,
            restaurantId = orderStartedEvent.restaurantId,
            items = mapOf(orderItemAddedEvent.productId to 1)
        )

        testFixture.given(orderStartedEvent, orderItemAddedEvent)
            .`when`(finalizeOrderCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderFinalizedEvent)
    }

    @Test
    fun `given finalized order, should mark order as paid`() {
        val orderStartedEvent = OrderStartedEvent(
            orderId = UUID.randomUUID(),
            userId = "user",
            restaurantId = UUID.randomUUID(),
            status = OrderStartedEvent.OrderStatus.CREATED
        )

        val orderFinalizedEvent = OrderFinalizedEvent(
            orderId = orderStartedEvent.orderId,
            userId = orderStartedEvent.userId,
            restaurantId = orderStartedEvent.restaurantId,
            items = mapOf(UUID.randomUUID() to 1)
        )

        val markOrderAsPaidCommand = MarkOrderAsPaidCommand(
            orderId = orderStartedEvent.orderId,
            userId = orderStartedEvent.userId
        )

        val orderPaidEvent = OrderPaidEvent(
            orderId = orderStartedEvent.orderId
        )

        testFixture.given(orderStartedEvent, orderFinalizedEvent)
            .`when`(markOrderAsPaidCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderPaidEvent)
    }

    @Test
    fun `given paid order, should mark order as delivered`() {
        val orderStartedEvent = OrderStartedEvent(
            orderId = UUID.randomUUID(),
            userId = "user",
            restaurantId = UUID.randomUUID(),
            status = OrderStartedEvent.OrderStatus.CREATED
        )

        val orderFinalizedEvent = OrderFinalizedEvent(
            orderId = orderStartedEvent.orderId,
            userId = orderStartedEvent.userId,
            restaurantId = orderStartedEvent.restaurantId,
            items = mapOf(UUID.randomUUID() to 1)
        )

        val orderPaidEvent = OrderPaidEvent(
            orderId = orderStartedEvent.orderId
        )

        val markOrderAsDeliveredCommand = MarkOrderAsDeliveredCommand(
            orderId = orderStartedEvent.orderId
        )

        val orderDeliveredEvent = OrderDeliveredEvent(
            orderId = orderStartedEvent.orderId
        )

        testFixture.given(orderStartedEvent, orderFinalizedEvent, orderPaidEvent)
            .`when`(markOrderAsDeliveredCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderDeliveredEvent)
    }

    @Test
    fun `given paid order, should reject order`() {
        val orderStartedEvent = OrderStartedEvent(
            orderId = UUID.randomUUID(),
            userId = "user",
            restaurantId = UUID.randomUUID(),
            status = OrderStartedEvent.OrderStatus.CREATED
        )

        val orderFinalizedEvent = OrderFinalizedEvent(
            orderId = orderStartedEvent.orderId,
            userId = orderStartedEvent.userId,
            restaurantId = orderStartedEvent.restaurantId,
            items = mapOf(UUID.randomUUID() to 1)
        )

        val orderPaidEvent = OrderPaidEvent(
            orderId = orderStartedEvent.orderId
        )

        val rejectOrderCommand = RejectOrderCommand(
            orderId = orderStartedEvent.orderId
        )

        val orderRejectedEvent = OrderRejectedEvent(
            orderId = orderStartedEvent.orderId
        )

        testFixture.given(orderStartedEvent, orderFinalizedEvent, orderPaidEvent)
            .`when`(rejectOrderCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderRejectedEvent)
    }

    @Test
    fun `given delivered order, should rate order`() {
        val orderStartedEvent = OrderStartedEvent(
            orderId = UUID.randomUUID(),
            userId = "user",
            restaurantId = UUID.randomUUID(),
            status = OrderStartedEvent.OrderStatus.CREATED
        )

        val orderFinalizedEvent = OrderFinalizedEvent(
            orderId = orderStartedEvent.orderId,
            userId = orderStartedEvent.userId,
            restaurantId = orderStartedEvent.restaurantId,
            items = mapOf(UUID.randomUUID() to 1)
        )

        val orderPaidEvent = OrderPaidEvent(
            orderId = orderStartedEvent.orderId
        )

        val orderDeliveredEvent = OrderDeliveredEvent(
            orderId = orderStartedEvent.orderId
        )

        val rateOrderCommand = RateOrderCommand(
            orderId = orderStartedEvent.orderId,
            userId = orderStartedEvent.userId,
            rating = 5
        )

        val orderRatedEvent = OrderRatedEvent(
            orderId = orderStartedEvent.orderId,
            userId = orderStartedEvent.userId,
            restaurantId = orderStartedEvent.restaurantId,
            rating = 5
        )

        testFixture.given(orderStartedEvent, orderFinalizedEvent, orderPaidEvent, orderDeliveredEvent)
            .`when`(rateOrderCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderRatedEvent)
    }
}
