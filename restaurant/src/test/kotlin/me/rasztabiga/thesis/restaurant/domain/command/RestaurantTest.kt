package me.rasztabiga.thesis.restaurant.domain.command

import me.rasztabiga.thesis.restaurant.domain.command.aggregate.Restaurant
import me.rasztabiga.thesis.restaurant.domain.command.command.CreateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.DeleteRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.UpdateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantDeletedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantUpdatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RestaurantTest {

    private lateinit var textFixture: AggregateTestFixture<Restaurant>

    @BeforeEach
    fun setUp() {
        textFixture = AggregateTestFixture(Restaurant::class.java)
    }

    @Test
    fun `should create restaurant`() {
        val createRestaurantCommand = CreateRestaurantCommand(
            UUID.randomUUID(),
            "Restaurant"
        )

        val restaurantCreatedEvent = RestaurantCreatedEvent(
            createRestaurantCommand.id,
            createRestaurantCommand.name
        )

        textFixture.givenNoPriorActivity()
            .`when`(createRestaurantCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(restaurantCreatedEvent)
    }

    @Test
    fun `should update restaurant`() {
        val updateRestaurantCommand = UpdateRestaurantCommand(
            UUID.randomUUID(),
            "New name"
        )

        val restaurantCreatedEvent = RestaurantCreatedEvent(
            updateRestaurantCommand.id,
            updateRestaurantCommand.name
        )

        val restaurantUpdatedEvent = RestaurantUpdatedEvent(
            updateRestaurantCommand.id,
            updateRestaurantCommand.name
        )

        textFixture.given(restaurantCreatedEvent)
            .`when`(updateRestaurantCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(restaurantUpdatedEvent)
    }

    @Test
    fun `should delete restaurant`() {
        val deleteRestaurantCommand = DeleteRestaurantCommand(
            UUID.randomUUID()
        )

        val restaurantCreatedEvent = RestaurantCreatedEvent(
            deleteRestaurantCommand.id,
            "Restaurant"
        )

        val restaurantDeletedEvent = RestaurantDeletedEvent(
            deleteRestaurantCommand.id
        )

        textFixture.given(restaurantCreatedEvent)
            .`when`(deleteRestaurantCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(restaurantDeletedEvent)
    }
}