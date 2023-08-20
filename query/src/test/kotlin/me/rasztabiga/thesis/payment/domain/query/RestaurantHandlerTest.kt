package me.rasztabiga.thesis.payment.domain.query

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.rasztabiga.thesis.query.domain.query.exception.RestaurantNotFoundException
import me.rasztabiga.thesis.query.domain.query.handler.RestaurantHandler
import me.rasztabiga.thesis.query.domain.query.query.FindAllRestaurantsQuery
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantAvailabilityUpdatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantDeletedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantMenuUpdatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantUpdatedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindRestaurantByIdQuery
import org.junit.jupiter.api.Test
import java.util.*

class RestaurantHandlerTest {

    private val restaurantRepository = InMemoryRestaurantRepository()

    private val restaurantHandler = RestaurantHandler(restaurantRepository)

    @Test
    fun `given restaurant created event, when handling FindAllRestaurantsQuery, then returns restaurant`() {
        // given
        val restaurantCreatedEvent = RestaurantCreatedEvent(UUID.randomUUID(), "Restaurant", "address")
        restaurantHandler.on(restaurantCreatedEvent)

        // when
        val restaurants = restaurantHandler.handle(FindAllRestaurantsQuery()).collectList().block()

        // then
        restaurants shouldNotBe null
        restaurants!!.size shouldBe 1
        restaurants[0].id shouldBe restaurantCreatedEvent.id
        restaurants[0].name shouldBe restaurantCreatedEvent.name
    }

    @Test
    fun `given restaurant created event, when handling FindRestaurantByIdQuery, then returns restaurant`() {
        // given
        val restaurantCreatedEvent = RestaurantCreatedEvent(UUID.randomUUID(), "Restaurant", "address")
        restaurantHandler.on(restaurantCreatedEvent)

        // when
        val restaurant = restaurantHandler.handle(FindRestaurantByIdQuery(restaurantCreatedEvent.id)).block()

        // then
        restaurant shouldNotBe null
        restaurant!!.id shouldBe restaurantCreatedEvent.id
        restaurant.name shouldBe restaurantCreatedEvent.name
    }

    @Test
    fun `given restaurant updated event, when handling FindRestaurantByIdQuery, then returns updated restaurant`() {
        // given
        val restaurantCreatedEvent = RestaurantCreatedEvent(UUID.randomUUID(), "Restaurant", "address")
        val restaurantUpdatedEvent = RestaurantUpdatedEvent(restaurantCreatedEvent.id, "New name")
        restaurantHandler.on(restaurantCreatedEvent)
        restaurantHandler.on(restaurantUpdatedEvent)

        // when
        val restaurant = restaurantHandler.handle(FindRestaurantByIdQuery(restaurantCreatedEvent.id)).block()

        // then
        restaurant shouldNotBe null
        restaurant!!.id shouldBe restaurantUpdatedEvent.id
        restaurant.name shouldBe restaurantUpdatedEvent.name
    }

    @Test
    fun `given restaurant deleted event, when handling FindRestaurantByIdQuery, then throws`() {
        // given
        val restaurantCreatedEvent = RestaurantCreatedEvent(UUID.randomUUID(), "Restaurant", "address")
        val restaurantDeletedEvent = RestaurantDeletedEvent(restaurantCreatedEvent.id)
        restaurantHandler.on(restaurantCreatedEvent)
        restaurantHandler.on(restaurantDeletedEvent)

        // when
        val exception = shouldThrowExactly<RestaurantNotFoundException> {
            restaurantHandler.handle(FindRestaurantByIdQuery(UUID.randomUUID())).block()
        }

        // then
        exception shouldNotBe null
    }

    @Test
    fun `given no restaurant created event, when handling FindRestaurantByIdQuery, then throws`() {
        // given

        // when
        val exception = shouldThrowExactly<RestaurantNotFoundException> {
            restaurantHandler.handle(FindRestaurantByIdQuery(UUID.randomUUID())).block()
        }

        // then
        exception shouldNotBe null
    }

    @Suppress("MaxLineLength")
    @Test
    fun `given restaurant availability updated event, when handling FindRestaurantByIdQuery, then returns updated restaurant`() {
        // given
        val restaurantCreatedEvent = RestaurantCreatedEvent(UUID.randomUUID(), "Restaurant", "address")
        val restaurantAvailabilityUpdatedEvent =
            RestaurantAvailabilityUpdatedEvent(
                restaurantCreatedEvent.id,
                RestaurantAvailabilityUpdatedEvent.Availability.OPEN
            )
        restaurantHandler.on(restaurantCreatedEvent)
        restaurantHandler.on(restaurantAvailabilityUpdatedEvent)

        // when
        val restaurant = restaurantHandler.handle(FindRestaurantByIdQuery(restaurantCreatedEvent.id)).block()

        // then
        restaurant shouldNotBe null
        restaurant!!.id shouldBe restaurantCreatedEvent.id
        restaurant.availability shouldBe RestaurantResponse.Availability.OPEN
    }

    @Suppress("MaxLineLength")
    @Test
    fun `given restaurant menu updated event, when handling FindRestaurantByIdQuery, then returns updated restaurant`() {
        // given
        val restaurantCreatedEvent = RestaurantCreatedEvent(UUID.randomUUID(), "Restaurant", "address")
        val restaurantMenuUpdatedEvent = RestaurantMenuUpdatedEvent(
            restaurantCreatedEvent.id,
            listOf(RestaurantMenuUpdatedEvent.Product(UUID.randomUUID(), "Product", "description", 1.0))
        )
        restaurantHandler.on(restaurantCreatedEvent)
        restaurantHandler.on(restaurantMenuUpdatedEvent)

        // when
        val restaurant = restaurantHandler.handle(FindRestaurantByIdQuery(restaurantCreatedEvent.id)).block()

        // then
        restaurant shouldNotBe null
        restaurant!!.id shouldBe restaurantCreatedEvent.id
        restaurant.menu.size shouldBe 1
        restaurant.menu[0].id shouldBe restaurantMenuUpdatedEvent.menu[0].id
        restaurant.menu[0].name shouldBe restaurantMenuUpdatedEvent.menu[0].name
        restaurant.menu[0].description shouldBe restaurantMenuUpdatedEvent.menu[0].description
        restaurant.menu[0].price shouldBe restaurantMenuUpdatedEvent.menu[0].price
    }
}