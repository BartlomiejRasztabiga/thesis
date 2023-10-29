package me.rasztabiga.thesis.restaurant.domain.command

import me.rasztabiga.thesis.restaurant.domain.command.aggregate.RestaurantOrder
import me.rasztabiga.thesis.restaurant.domain.command.command.AcceptRestaurantOrderCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.PrepareRestaurantOrderCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.RejectRestaurantOrderCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreateRestaurantOrderCommand
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderPreparedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderRejectedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RestaurantOrderTest {

    private lateinit var testFixture: AggregateTestFixture<RestaurantOrder>

    @BeforeEach
    fun setUp() {
        testFixture = AggregateTestFixture(RestaurantOrder::class.java)
    }

    @Test
    fun `should create restaurant order`() {
        val createRestaurantOrderCommand = CreateRestaurantOrderCommand(
            restaurantOrderId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantId = UUID.randomUUID(),
            items = mapOf(UUID.randomUUID() to 1)
        )

        val restaurantOrderCreatedEvent = RestaurantOrderCreatedEvent(
            restaurantOrderId = createRestaurantOrderCommand.restaurantOrderId,
            orderId = createRestaurantOrderCommand.orderId,
            restaurantId = createRestaurantOrderCommand.restaurantId,
            items = createRestaurantOrderCommand.items
        )

        testFixture.givenNoPriorActivity()
            .`when`(createRestaurantOrderCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(restaurantOrderCreatedEvent)
    }

    @Test
    fun `given restaurant order, should accept it`() {
        val restaurantOrderCreatedEvent = RestaurantOrderCreatedEvent(
            restaurantOrderId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantId = UUID.randomUUID(),
            items = mapOf(UUID.randomUUID() to 1)
        )

        val acceptRestaurantOrderCommand = AcceptRestaurantOrderCommand(
            restaurantOrderId = restaurantOrderCreatedEvent.restaurantOrderId,
            restaurantId = restaurantOrderCreatedEvent.restaurantId
        )

        val restaurantOrderAcceptedEvent = RestaurantOrderAcceptedEvent(
            restaurantOrderId = restaurantOrderCreatedEvent.restaurantOrderId,
            orderId = restaurantOrderCreatedEvent.orderId,
            restaurantId = restaurantOrderCreatedEvent.restaurantId
        )

        testFixture.given(restaurantOrderCreatedEvent)
            .`when`(acceptRestaurantOrderCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(restaurantOrderAcceptedEvent)
    }

    @Test
    fun `given restaurant order, should reject it`() {
        val restaurantOrderCreatedEvent = RestaurantOrderCreatedEvent(
            restaurantOrderId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantId = UUID.randomUUID(),
            items = mapOf(UUID.randomUUID() to 1)
        )

        val rejectRestaurantOrderCommand = RejectRestaurantOrderCommand(
            restaurantOrderId = restaurantOrderCreatedEvent.restaurantOrderId,
            restaurantId = restaurantOrderCreatedEvent.restaurantId
        )

        val restaurantOrderRejectedEvent = RestaurantOrderRejectedEvent(
            restaurantOrderId = restaurantOrderCreatedEvent.restaurantOrderId,
            orderId = restaurantOrderCreatedEvent.orderId
        )

        testFixture.given(restaurantOrderCreatedEvent)
            .`when`(rejectRestaurantOrderCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(restaurantOrderRejectedEvent)
    }

    @Test
    fun `given accepted restaurant order, should prepare it`() {
        val restaurantOrderCreatedEvent = RestaurantOrderCreatedEvent(
            restaurantOrderId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantId = UUID.randomUUID(),
            items = mapOf(UUID.randomUUID() to 1)
        )

        val restaurantOrderAcceptedEvent = RestaurantOrderAcceptedEvent(
            restaurantOrderId = restaurantOrderCreatedEvent.restaurantOrderId,
            orderId = restaurantOrderCreatedEvent.orderId,
            restaurantId = restaurantOrderCreatedEvent.restaurantId
        )

        val prepareRestaurantOrderCommand = PrepareRestaurantOrderCommand(
            restaurantOrderId = restaurantOrderCreatedEvent.restaurantOrderId,
            restaurantId = restaurantOrderCreatedEvent.restaurantId
        )

        val restaurantOrderPreparedEvent = RestaurantOrderPreparedEvent(
            restaurantOrderId = restaurantOrderCreatedEvent.restaurantOrderId,
            orderId = restaurantOrderCreatedEvent.orderId,
            restaurantId = restaurantOrderCreatedEvent.restaurantId
        )

        testFixture.given(restaurantOrderCreatedEvent, restaurantOrderAcceptedEvent)
            .`when`(prepareRestaurantOrderCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(restaurantOrderPreparedEvent)
    }
}
