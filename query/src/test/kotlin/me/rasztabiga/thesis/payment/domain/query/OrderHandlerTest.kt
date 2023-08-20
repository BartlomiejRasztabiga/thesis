package me.rasztabiga.thesis.payment.domain.query

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.rasztabiga.thesis.query.domain.query.handler.OrderHandler
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.shared.domain.command.event.OrderCanceledEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderFinalizedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderItemAddedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderItemDeletedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderStartedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderTotalCalculatedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindOrderByIdQuery
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class OrderHandlerTest {

    private val orderRepository = InMemoryOrderRepository()

    private val orderHandler = OrderHandler(orderRepository)

    @Test
    fun `given order started event, when handling FindOrderByIdQuery, then returns order`() {
        // given
        val orderStartedEvent =
            OrderStartedEvent(UUID.randomUUID(), UUID.randomUUID(), "", OrderStartedEvent.OrderStatus.CREATED)
        orderHandler.on(orderStartedEvent)

        // when
        val order = orderHandler.handle(FindOrderByIdQuery(orderStartedEvent.orderId)).block()

        // then
        order shouldNotBe null
        order!!.id shouldBe orderStartedEvent.orderId
        order.status.name shouldBe orderStartedEvent.status.name
    }

    @Test
    fun `given order item added event, when handling FindOrderByIdQuery, then returns order`() {
        // given
        val orderStartedEvent =
            OrderStartedEvent(UUID.randomUUID(), UUID.randomUUID(), "", OrderStartedEvent.OrderStatus.CREATED)
        orderHandler.on(orderStartedEvent)
        val orderItemAddedEvent = OrderItemAddedEvent(orderStartedEvent.orderId, UUID.randomUUID(), UUID.randomUUID())
        orderHandler.on(orderItemAddedEvent)

        // when
        val order = orderHandler.handle(FindOrderByIdQuery(orderStartedEvent.orderId)).block()

        // then
        order shouldNotBe null
        order!!.id shouldBe orderStartedEvent.orderId
        order.items.size shouldBe 1
        order.items[0].id shouldBe orderItemAddedEvent.orderItemId
        order.items[0].productId shouldBe orderItemAddedEvent.productId
    }

    @Test
    fun `given order item deleted event, when handling FindOrderByIdQuery, then returns order`() {
        // given
        val orderStartedEvent =
            OrderStartedEvent(UUID.randomUUID(), UUID.randomUUID(), "", OrderStartedEvent.OrderStatus.CREATED)
        orderHandler.on(orderStartedEvent)
        val orderItemAddedEvent = OrderItemAddedEvent(orderStartedEvent.orderId, UUID.randomUUID(), UUID.randomUUID())
        orderHandler.on(orderItemAddedEvent)
        val orderItemDeletedEvent = OrderItemDeletedEvent(orderStartedEvent.orderId, orderItemAddedEvent.orderItemId)
        orderHandler.on(orderItemDeletedEvent)

        // when
        val order = orderHandler.handle(FindOrderByIdQuery(orderStartedEvent.orderId)).block()

        // then
        order shouldNotBe null
        order!!.id shouldBe orderStartedEvent.orderId
        order.items.size shouldBe 0
    }

    @Test
    fun `given order canceled event, when handling FindOrderByIdQuery, then returns order`() {
        // given
        val orderStartedEvent =
            OrderStartedEvent(UUID.randomUUID(), UUID.randomUUID(), "", OrderStartedEvent.OrderStatus.CREATED)
        orderHandler.on(orderStartedEvent)
        val orderCanceledEvent = OrderCanceledEvent(orderStartedEvent.orderId)
        orderHandler.on(orderCanceledEvent)

        // when
        val order = orderHandler.handle(FindOrderByIdQuery(orderStartedEvent.orderId)).block()

        // then
        order shouldNotBe null
        order!!.id shouldBe orderStartedEvent.orderId
        order.status shouldBe OrderResponse.OrderStatus.CANCELED
    }

    @Test
    fun `given order finalized event, when handling FindOrderByIdQuery, then returns order`() {
        // given
        val orderStartedEvent =
            OrderStartedEvent(UUID.randomUUID(), UUID.randomUUID(), "", OrderStartedEvent.OrderStatus.CREATED)
        orderHandler.on(orderStartedEvent)
        val orderFinalizedEvent =
            OrderFinalizedEvent(
                orderStartedEvent.orderId,
                orderStartedEvent.userId,
                orderStartedEvent.restaurantId,
                emptyList(),
                UUID.randomUUID()
            )
        orderHandler.on(orderFinalizedEvent)

        // when
        val order = orderHandler.handle(FindOrderByIdQuery(orderStartedEvent.orderId)).block()

        // then
        order shouldNotBe null
        order!!.id shouldBe orderStartedEvent.orderId
        order.status shouldBe OrderResponse.OrderStatus.FINALIZED
    }

    @Test
    fun `given order total calculated event, when handling FindOrderByIdQuery, then returns order`() {
        // given
        val orderStartedEvent =
            OrderStartedEvent(UUID.randomUUID(), UUID.randomUUID(), "", OrderStartedEvent.OrderStatus.CREATED)
        orderHandler.on(orderStartedEvent)
        val orderTotalCalculatedEvent = OrderTotalCalculatedEvent(orderStartedEvent.orderId, BigDecimal.valueOf(10.0))
        orderHandler.on(orderTotalCalculatedEvent)

        // when
        val order = orderHandler.handle(FindOrderByIdQuery(orderStartedEvent.orderId)).block()

        // then
        order shouldNotBe null
        order!!.id shouldBe orderStartedEvent.orderId
        order.total shouldBe orderTotalCalculatedEvent.total
    }
}