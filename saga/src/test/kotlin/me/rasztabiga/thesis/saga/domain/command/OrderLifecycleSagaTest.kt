package me.rasztabiga.thesis.saga.domain.command

import me.rasztabiga.thesis.saga.domain.command.saga.OrderLifecycleSaga
import me.rasztabiga.thesis.shared.domain.command.event.OrderFinalizedEvent
import org.axonframework.test.saga.SagaTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class OrderLifecycleSagaTest {

    private lateinit var testFixture: SagaTestFixture<OrderLifecycleSaga>

    @BeforeEach
    fun setUp() {
        testFixture = SagaTestFixture(OrderLifecycleSaga::class.java)
    }

    @Test
    fun `should execute saga`() {
        val orderFinalizedEvent = OrderFinalizedEvent(
            orderId = UUID.randomUUID(),
            userId = UUID.randomUUID().toString(),
            restaurantId = UUID.randomUUID(),
            items = mapOf(
                UUID.randomUUID() to 1,
                UUID.randomUUID() to 2
            )
        )
    }
}
