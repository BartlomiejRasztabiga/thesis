package me.rasztabiga.thesis.saga.domain.command

import io.mockk.every
import io.mockk.mockk
import me.rasztabiga.thesis.saga.domain.command.saga.OrderLifecycleSaga
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.shared.domain.command.event.OrderFinalizedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindOrderByIdQuery
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.test.saga.SagaTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.CompletableFuture

class OrderLifecycleSagaTest {

    private lateinit var testFixture: SagaTestFixture<OrderLifecycleSaga>
    private lateinit var queryGateway: QueryGateway

    @BeforeEach
    fun setUp() {
        queryGateway = mockk<QueryGateway>()

        testFixture = SagaTestFixture(OrderLifecycleSaga::class.java)
        testFixture.registerResource(queryGateway)
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

        every {
            queryGateway.query(
                FindOrderByIdQuery(orderFinalizedEvent.orderId), ResponseTypes.instanceOf(
                    OrderResponse::class.java
                )
            )
        } returns CompletableFuture.completedFuture(mockk<OrderResponse>())

        testFixture
            .whenPublishingA(orderFinalizedEvent)
            .expectActiveSagas(1)
    }
}
