package me.rasztabiga.thesis.order.domain.command

import me.rasztabiga.thesis.order.domain.command.aggregate.Order
import me.rasztabiga.thesis.order.domain.command.aggregate.OrderStatus
import me.rasztabiga.thesis.order.domain.command.command.AddOrderItemCommand
import me.rasztabiga.thesis.order.domain.command.command.DeleteOrderItemCommand
import me.rasztabiga.thesis.order.domain.command.command.StartOrderCommand
import me.rasztabiga.thesis.order.domain.command.event.OrderItemAddedEvent
import me.rasztabiga.thesis.order.domain.command.event.OrderItemDeletedEvent
import me.rasztabiga.thesis.order.domain.command.event.OrderStartedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class OrderTest {

    private lateinit var testFixture: AggregateTestFixture<Order>

    @BeforeEach
    fun setUp() {
        testFixture = AggregateTestFixture(Order::class.java)
        // TODO how to test StartOrderCommandInterceptor?
    }

    @Test
    fun `should create order`() {
        val startOrderCommand = StartOrderCommand(
            UUID.randomUUID(),
            "userId",
            UUID.randomUUID()
        )

        val orderStartedEvent = OrderStartedEvent(
            startOrderCommand.orderId,
            startOrderCommand.restaurantId,
            startOrderCommand.userId,
            OrderStatus.CREATED
        )

        testFixture.givenNoPriorActivity()
            .`when`(startOrderCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderStartedEvent)
    }

    @Test
    fun `should add order item`() {
        val orderStartedEvent = OrderStartedEvent(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "userId",
            OrderStatus.CREATED
        )

        val addOrderItemCommand = AddOrderItemCommand(
            orderStartedEvent.orderId,
            orderStartedEvent.userId,
            UUID.randomUUID(),
            UUID.randomUUID()
        )

        val orderItemAddedEvent = OrderItemAddedEvent(
            addOrderItemCommand.orderId,
            addOrderItemCommand.orderItemId,
            addOrderItemCommand.productId
        )

        testFixture.given(orderStartedEvent)
            .`when`(addOrderItemCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderItemAddedEvent)
    }

    @Test
    fun `should delete order item`() {
        val orderStartedEvent = OrderStartedEvent(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "userId",
            OrderStatus.CREATED
        )

        val orderItemAddedEvent = OrderItemAddedEvent(
            orderStartedEvent.orderId,
            UUID.randomUUID(),
            UUID.randomUUID()
        )

        val deleteOrderItemCommand = DeleteOrderItemCommand(
            orderStartedEvent.orderId,
            orderStartedEvent.userId,
            orderItemAddedEvent.orderItemId
        )

        val orderItemDeletedEvent = OrderItemDeletedEvent(
            deleteOrderItemCommand.orderId,
            deleteOrderItemCommand.orderItemId
        )

        testFixture.given(orderStartedEvent, orderItemAddedEvent)
            .`when`(deleteOrderItemCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderItemDeletedEvent)
    }
}
