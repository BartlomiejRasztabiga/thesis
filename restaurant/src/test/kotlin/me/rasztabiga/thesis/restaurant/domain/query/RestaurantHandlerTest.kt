package me.rasztabiga.thesis.restaurant.domain.query

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.Availability
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantAvailabilityUpdatedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantDeletedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantUpdatedEvent
import me.rasztabiga.thesis.restaurant.domain.query.exception.RestaurantNotFoundException
import me.rasztabiga.thesis.restaurant.domain.query.handler.RestaurantHandler
import me.rasztabiga.thesis.restaurant.domain.query.query.FindAllRestaurantsQuery
import me.rasztabiga.thesis.restaurant.domain.query.query.FindRestaurantByIdQuery
import org.junit.jupiter.api.Test
import java.util.*
import me.rasztabiga.thesis.restaurant.domain.command.aggregate.Availability as DomainAvailability

class RestaurantHandlerTest {

    private val restaurantRepository = InMemoryRestaurantRepository()

    private val restaurantHandler = RestaurantHandler(restaurantRepository)

    @Test
    fun `given restaurant created event, when handling FindAllRestaurantsQuery, then returns restaurant`() {
        // given
        val restaurantCreatedEvent = RestaurantCreatedEvent(UUID.randomUUID(), "Restaurant", DomainAvailability.CLOSED)
        restaurantHandler.on(restaurantCreatedEvent)

        // when
        val restaurants = restaurantHandler.on(FindAllRestaurantsQuery()).collectList().block()

        // then
        restaurants shouldNotBe null
        restaurants!!.size shouldBe 1
        restaurants[0].id shouldBe restaurantCreatedEvent.id
        restaurants[0].name shouldBe restaurantCreatedEvent.name
    }

    @Test
    fun `given restaurant created event, when handling FindRestaurantByIdQuery, then returns restaurant`() {
        // given
        val restaurantCreatedEvent = RestaurantCreatedEvent(UUID.randomUUID(), "Restaurant", DomainAvailability.CLOSED)
        restaurantHandler.on(restaurantCreatedEvent)

        // when
        val restaurant = restaurantHandler.on(FindRestaurantByIdQuery(restaurantCreatedEvent.id)).block()

        // then
        restaurant shouldNotBe null
        restaurant!!.id shouldBe restaurantCreatedEvent.id
        restaurant.name shouldBe restaurantCreatedEvent.name
    }

    @Test
    fun `given restaurant updated event, when handling FindRestaurantByIdQuery, then returns updated restaurant`() {
        // given
        val restaurantCreatedEvent = RestaurantCreatedEvent(UUID.randomUUID(), "Restaurant", DomainAvailability.CLOSED)
        val restaurantUpdatedEvent = RestaurantUpdatedEvent(restaurantCreatedEvent.id, "New name")
        restaurantHandler.on(restaurantCreatedEvent)
        restaurantHandler.on(restaurantUpdatedEvent)

        // when
        val restaurant = restaurantHandler.on(FindRestaurantByIdQuery(restaurantCreatedEvent.id)).block()

        // then
        restaurant shouldNotBe null
        restaurant!!.id shouldBe restaurantUpdatedEvent.id
        restaurant.name shouldBe restaurantUpdatedEvent.name
    }

    @Test
    fun `given restaurant deleted event, when handling FindRestaurantByIdQuery, then throws`() {
        // given
        val restaurantCreatedEvent = RestaurantCreatedEvent(UUID.randomUUID(), "Restaurant", DomainAvailability.CLOSED)
        val restaurantDeletedEvent = RestaurantDeletedEvent(restaurantCreatedEvent.id)
        restaurantHandler.on(restaurantCreatedEvent)
        restaurantHandler.on(restaurantDeletedEvent)

        // when
        val exception = shouldThrowExactly<RestaurantNotFoundException> {
            restaurantHandler.on(FindRestaurantByIdQuery(UUID.randomUUID())).block()
        }

        // then
        exception shouldNotBe null
    }

    @Test
    fun `given no restaurant created event, when handling FindRestaurantByIdQuery, then throws`() {
        // given

        // when
        val exception = shouldThrowExactly<RestaurantNotFoundException> {
            restaurantHandler.on(FindRestaurantByIdQuery(UUID.randomUUID())).block()
        }

        // then
        exception shouldNotBe null
    }

    @Suppress("MaxLineLength")
    @Test
    fun `given restaurant availability updated event, when handling FindRestaurantByIdQuery, then returns updated restaurant`() {
        // given
        val restaurantCreatedEvent = RestaurantCreatedEvent(UUID.randomUUID(), "Restaurant", DomainAvailability.CLOSED)
        val restaurantAvailabilityUpdatedEvent =
            RestaurantAvailabilityUpdatedEvent(restaurantCreatedEvent.id, DomainAvailability.OPEN)
        restaurantHandler.on(restaurantCreatedEvent)
        restaurantHandler.on(restaurantAvailabilityUpdatedEvent)

        // when
        val restaurant = restaurantHandler.on(FindRestaurantByIdQuery(restaurantCreatedEvent.id)).block()

        // then
        restaurant shouldNotBe null
        restaurant!!.id shouldBe restaurantCreatedEvent.id
        restaurant.name shouldBe restaurantCreatedEvent.name
        restaurant.availability shouldBe Availability.OPEN
    }
}
