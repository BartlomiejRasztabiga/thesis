package me.rasztabiga.thesis.restaurant.domain.command

import me.rasztabiga.thesis.restaurant.domain.command.aggregate.Restaurant
import me.rasztabiga.thesis.restaurant.domain.command.command.CreateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantCreatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

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
}
