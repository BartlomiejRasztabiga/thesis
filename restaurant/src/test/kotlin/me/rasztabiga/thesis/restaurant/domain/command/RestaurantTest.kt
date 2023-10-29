package me.rasztabiga.thesis.restaurant.domain.command

import io.mockk.every
import io.mockk.mockk
import me.rasztabiga.thesis.restaurant.domain.command.aggregate.Availability
import me.rasztabiga.thesis.restaurant.domain.command.aggregate.Product
import me.rasztabiga.thesis.restaurant.domain.command.aggregate.Restaurant
import me.rasztabiga.thesis.restaurant.domain.command.command.CreateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.DeleteRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.UpdateRestaurantAvailabilityCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.UpdateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.UpdateRestaurantMenuCommand
import me.rasztabiga.thesis.restaurant.domain.command.port.CalculateDeliveryFeePort
import me.rasztabiga.thesis.restaurant.domain.command.port.GeocodeAddressPort
import me.rasztabiga.thesis.restaurant.domain.command.port.RestaurantVerificationPort
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import me.rasztabiga.thesis.shared.domain.command.command.CalculateOrderTotalCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderTotalCalculatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantAvailabilityUpdatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantDeletedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantMenuUpdatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantUpdatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RestaurantTest {

    private lateinit var testFixture: AggregateTestFixture<Restaurant>
    private lateinit var geocodeAddressPort: GeocodeAddressPort
    private lateinit var verificationPort: RestaurantVerificationPort
    private lateinit var calculateDeliveryFeePort: CalculateDeliveryFeePort

    @BeforeEach
    fun setUp() {
        geocodeAddressPort = mockk<GeocodeAddressPort>()
        verificationPort = mockk<RestaurantVerificationPort>()
        calculateDeliveryFeePort = mockk<CalculateDeliveryFeePort>()

        testFixture = AggregateTestFixture(Restaurant::class.java)
        testFixture.registerInjectableResource(geocodeAddressPort)
        testFixture.registerInjectableResource(verificationPort)
        testFixture.registerInjectableResource(calculateDeliveryFeePort)
    }

    @Test
    fun `should create restaurant`() {
        val createRestaurantCommand = CreateRestaurantCommand(
            id = UUID.randomUUID(),
            managerId = "managerId",
            email = "email",
            name = "name",
            address = "address",
            imageUrl = "imageUrl"
        )

        every { verificationPort.verifyRestaurantByManagerIdExists(createRestaurantCommand.managerId) } returns false
        val location = Location(lat = 0.0, lng = 0.0, streetAddress = null)
        every { geocodeAddressPort.geocode(createRestaurantCommand.address) } returns location

        val restaurantCreatedEvent = RestaurantCreatedEvent(
            restaurantId = createRestaurantCommand.id,
            name = createRestaurantCommand.name,
            location = location,
            managerId = createRestaurantCommand.managerId,
            email = createRestaurantCommand.email,
            imageUrl = createRestaurantCommand.imageUrl
        )

        testFixture.givenNoPriorActivity()
            .`when`(createRestaurantCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(restaurantCreatedEvent)
    }

    @Test
    fun `given restaurant, should update restaurant`() {
        val restaurantCreatedEvent = RestaurantCreatedEvent(
            restaurantId = UUID.randomUUID(),
            name = "name",
            location = Location(lat = 0.0, lng = 0.0, streetAddress = null),
            managerId = "managerId",
            email = "email",
            imageUrl = "imageUrl"
        )

        val updateRestaurantCommand = UpdateRestaurantCommand(
            id = restaurantCreatedEvent.restaurantId,
            name = "newName"
        )

        val restaurantUpdatedEvent = RestaurantUpdatedEvent(
            id = updateRestaurantCommand.id,
            name = updateRestaurantCommand.name
        )

        testFixture.given(restaurantCreatedEvent)
            .`when`(updateRestaurantCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(restaurantUpdatedEvent)
    }

    @Test
    fun `given restaurant, should delete restaurant`() {
        val restaurantCreatedEvent = RestaurantCreatedEvent(
            restaurantId = UUID.randomUUID(),
            name = "name",
            location = Location(lat = 0.0, lng = 0.0, streetAddress = null),
            managerId = "managerId",
            email = "email",
            imageUrl = "imageUrl"
        )

        val deleteRestaurantCommand = DeleteRestaurantCommand(
            id = restaurantCreatedEvent.restaurantId
        )

        val restaurantDeletedEvent = RestaurantDeletedEvent(
            id = deleteRestaurantCommand.id
        )

        testFixture.given(restaurantCreatedEvent)
            .`when`(deleteRestaurantCommand)
            .expectSuccessfulHandlerExecution()
            .expectMarkedDeleted()
            .expectEvents(restaurantDeletedEvent)
    }

    @Test
    fun `given restaurant, should update restaurant availability`() {
        val restaurantCreatedEvent = RestaurantCreatedEvent(
            restaurantId = UUID.randomUUID(),
            name = "name",
            location = Location(lat = 0.0, lng = 0.0, streetAddress = null),
            managerId = "managerId",
            email = "email",
            imageUrl = "imageUrl"
        )

        val updateRestaurantAvailabilityCommand = UpdateRestaurantAvailabilityCommand(
            id = restaurantCreatedEvent.restaurantId,
            availability = Availability.OPEN
        )

        val restaurantAvailabilityUpdatedEvent = RestaurantAvailabilityUpdatedEvent(
            id = updateRestaurantAvailabilityCommand.id,
            availability = RestaurantAvailabilityUpdatedEvent.Availability.valueOf(
                updateRestaurantAvailabilityCommand.availability.name
            )
        )

        testFixture.given(restaurantCreatedEvent)
            .`when`(updateRestaurantAvailabilityCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(restaurantAvailabilityUpdatedEvent)
    }

    @Test
    fun `given restaurant, should update restaurant menu`() {
        val restaurantCreatedEvent = RestaurantCreatedEvent(
            restaurantId = UUID.randomUUID(),
            name = "name",
            location = Location(lat = 0.0, lng = 0.0, streetAddress = null),
            managerId = "managerId",
            email = "email",
            imageUrl = "imageUrl"
        )

        val updateRestaurantMenuCommand = UpdateRestaurantMenuCommand(
            id = restaurantCreatedEvent.restaurantId,
            menu = listOf(
                Product(
                    id = UUID.randomUUID(),
                    name = "name",
                    description = "description",
                    price = 1.0.toBigDecimal(),
                    imageUrl = "imageUrl"
                )
            )
        )

        val restaurantMenuUpdatedEvent = RestaurantMenuUpdatedEvent(
            id = updateRestaurantMenuCommand.id,
            menu = updateRestaurantMenuCommand.menu.map {
                RestaurantMenuUpdatedEvent.Product(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    price = it.price,
                    imageUrl = it.imageUrl
                )
            }
        )

        testFixture.given(restaurantCreatedEvent)
            .`when`(updateRestaurantMenuCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(restaurantMenuUpdatedEvent)
    }

    @Test
    fun `given restaurant, should calculate order total`() {
        val restaurantCreatedEvent = RestaurantCreatedEvent(
            restaurantId = UUID.randomUUID(),
            name = "name",
            location = Location(lat = 0.0, lng = 0.0, streetAddress = null),
            managerId = "managerId",
            email = "email",
            imageUrl = "imageUrl"
        )

        val restaurantMenuUpdatedEvent = RestaurantMenuUpdatedEvent(
            id = restaurantCreatedEvent.restaurantId,
            menu = listOf(
                RestaurantMenuUpdatedEvent.Product(
                    id = UUID.randomUUID(),
                    name = "name",
                    description = "description",
                    price = 2.0.toBigDecimal(),
                    imageUrl = "imageUrl"
                )
            )
        )

        val calculateOrderTotalCommand = CalculateOrderTotalCommand(
            orderId = UUID.randomUUID(),
            restaurantId = restaurantCreatedEvent.restaurantId,
            items = mapOf(
                restaurantMenuUpdatedEvent.menu[0].id to 3
            ),
            restaurantAddress = "restaurantAddress",
            deliveryAddress = "deliveryAddress"
        )

        every {
            calculateDeliveryFeePort.calculateDeliveryFee(
                calculateOrderTotalCommand.restaurantAddress,
                calculateOrderTotalCommand.deliveryAddress
            )
        } returns 10.0.toBigDecimal()

        val orderTotalCalculatedEvent = OrderTotalCalculatedEvent(
            orderId = calculateOrderTotalCommand.orderId,
            productsTotal = 6.0.toBigDecimal(),
            deliveryFee = 10.0.toBigDecimal()
        )

        testFixture.given(restaurantCreatedEvent, restaurantMenuUpdatedEvent)
            .`when`(calculateOrderTotalCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderTotalCalculatedEvent)
    }
}
