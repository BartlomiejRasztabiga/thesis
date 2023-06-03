package me.rasztabiga.thesis.order.domain.query

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.rasztabiga.thesis.order.domain.command.aggregate.OrderStatus
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
}
