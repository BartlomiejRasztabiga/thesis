package me.rasztabiga.thesis.restaurant.domain.query

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.restaurant.domain.query.handler.RestaurantHandler
import me.rasztabiga.thesis.restaurant.domain.query.query.FindAllRestaurantsQuery
import org.junit.jupiter.api.Test
import java.util.UUID

class RestaurantHandlerTest {

    private val restaurantRepository = InMemoryRestaurantRepository()

    private val restaurantHandler = RestaurantHandler(restaurantRepository)

    @Test
    fun `given restaurant created event, when handling FindAllRestaurantsQuery, then returns restaurant`() {
        // given
        val restaurantCreatedEvent = RestaurantCreatedEvent(UUID.randomUUID(), "Restaurant")
        restaurantHandler.on(restaurantCreatedEvent)

        // when
        val restaurants = restaurantHandler.on(FindAllRestaurantsQuery()).collectList().block()

        // then
        restaurants shouldNotBe null
        restaurants!!.size shouldBe 1
        restaurants[0].id shouldBe restaurantCreatedEvent.id
        restaurants[0].name shouldBe restaurantCreatedEvent.name
    }
}
