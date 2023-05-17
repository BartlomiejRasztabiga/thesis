package me.rasztabiga.thesis.order.domain.command

import me.rasztabiga.thesis.order.domain.command.aggregate.Order
import me.rasztabiga.thesis.order.domain.command.aggregate.OrderStatus
import me.rasztabiga.thesis.order.domain.command.command.StartOrderCommand
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
            OrderStatus.CREATED
        )

        testFixture.givenNoPriorActivity()
            .`when`(startOrderCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderStartedEvent)
    }
}
