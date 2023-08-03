package me.rasztabiga.thesis.order.domain.query

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.order.domain.command.aggregate.OrderStatus
import me.rasztabiga.thesis.order.domain.command.event.OrderCanceledEvent
import me.rasztabiga.thesis.order.domain.command.event.OrderItemAddedEvent
import me.rasztabiga.thesis.order.domain.command.event.OrderItemDeletedEvent
import me.rasztabiga.thesis.order.domain.command.event.OrderStartedEvent
import me.rasztabiga.thesis.order.domain.query.handler.OrderHandler
import me.rasztabiga.thesis.order.domain.query.query.FindOrderByIdQuery
import org.junit.jupiter.api.Test
import java.util.*

class OrderHandlerTest {

    private val orderRepository = InMemoryOrderRepository()

    private val orderHandler = OrderHandler(orderRepository)

    @Test
    fun `given order started event, when handling FindOrderByIdQuery, then returns order`() {
        // given
        val orderStartedEvent = OrderStartedEvent(UUID.randomUUID(), UUID.randomUUID(), "", OrderStatus.CREATED)
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
        val orderStartedEvent = OrderStartedEvent(UUID.randomUUID(), UUID.randomUUID(), "", OrderStatus.CREATED)
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
        val orderStartedEvent = OrderStartedEvent(UUID.randomUUID(), UUID.randomUUID(), "", OrderStatus.CREATED)
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
        val orderStartedEvent = OrderStartedEvent(UUID.randomUUID(), UUID.randomUUID(), "", OrderStatus.CREATED)
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
}
