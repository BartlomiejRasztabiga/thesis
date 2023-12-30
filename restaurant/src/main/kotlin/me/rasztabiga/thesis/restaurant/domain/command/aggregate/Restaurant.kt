package me.rasztabiga.thesis.restaurant.domain.command.aggregate

import me.rasztabiga.thesis.restaurant.domain.command.command.CreateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.DeleteRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.UpdateRestaurantAvailabilityCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.UpdateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.UpdateRestaurantMenuCommand
import me.rasztabiga.thesis.restaurant.domain.command.port.CalculateDeliveryFeePort
import me.rasztabiga.thesis.restaurant.domain.command.port.GeocodeAddressPort
import me.rasztabiga.thesis.restaurant.domain.command.port.RestaurantVerificationPort
import me.rasztabiga.thesis.shared.domain.command.command.CalculateOrderTotalCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderTotalCalculatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantAvailabilityUpdatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantDeletedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantMenuUpdatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantUpdatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateLifecycle.markDeleted
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal
import java.util.*

@Aggregate
internal class Restaurant {

    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var managerId: String
    private lateinit var name: String
    private var availability: Availability = Availability.CLOSED
    private var menu: List<Product> = listOf()

    constructor()

    @CommandHandler
    constructor(
        command: CreateRestaurantCommand,
        geocodeAddressPort: GeocodeAddressPort,
        verificationPort: RestaurantVerificationPort
    ) {
        require(!verificationPort.verifyRestaurantByManagerIdExists(command.managerId)) {
            "Restaurant for manager already exists"
        }

        val location = geocodeAddressPort.geocode(command.address)

        apply(
            RestaurantCreatedEvent(
                restaurantId = command.id,
                name = command.name,
                location = location,
                managerId = command.managerId,
                email = command.email,
                imageUrl = command.imageUrl
            )
        )
    }

    @CommandHandler
    fun handle(command: UpdateRestaurantCommand) {
        apply(RestaurantUpdatedEvent(id = command.id, name = command.name))
    }

    @CommandHandler
    fun handle(command: DeleteRestaurantCommand) {
        apply(RestaurantDeletedEvent(id = command.id))
    }

    @CommandHandler
    fun handle(command: UpdateRestaurantAvailabilityCommand) {
        apply(
            RestaurantAvailabilityUpdatedEvent(
                id = command.id,
                availability = RestaurantAvailabilityUpdatedEvent.Availability.valueOf(command.availability.name)
            )
        )
    }

    @CommandHandler
    fun handle(command: UpdateRestaurantMenuCommand) {
        command.menu.forEach { product ->
            require(product.price >= BigDecimal.ZERO) { "Price cannot be negative" }
        }

        apply(RestaurantMenuUpdatedEvent(id = command.id, menu = command.menu.map {
            RestaurantMenuUpdatedEvent.Product(it.id, it.name, it.description, it.price, it.imageUrl)
        }))
    }

    @CommandHandler
    fun handle(command: CalculateOrderTotalCommand, calculateDeliveryFeePort: CalculateDeliveryFeePort) {
        var total = BigDecimal.ZERO
        command.items.forEach { (productId, quantity) ->
            val product = menu.find { it.id == productId } ?: error("Product not found")
            total += product.price * BigDecimal(quantity)
        }

        val deliveryFee =
            calculateDeliveryFeePort.calculateDeliveryFee(command.restaurantAddress, command.deliveryAddress)

        apply(
            OrderTotalCalculatedEvent(
                orderId = command.orderId,
                restaurantId = command.restaurantId,
                productsTotal = total,
                deliveryFee = deliveryFee,
                items = command.items
            )
        )
    }

    @EventSourcingHandler
    fun on(event: RestaurantCreatedEvent) {
        this.id = event.restaurantId
        this.name = event.name
        this.managerId = event.managerId
    }

    @EventSourcingHandler
    fun on(event: RestaurantUpdatedEvent) {
        this.name = event.name
    }

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: RestaurantDeletedEvent) {
        markDeleted()
    }

    @EventSourcingHandler
    fun on(event: RestaurantAvailabilityUpdatedEvent) {
        this.availability = Availability.valueOf(event.availability.name)
    }

    @EventSourcingHandler
    fun on(event: RestaurantMenuUpdatedEvent) {
        this.menu = event.menu.map {
            Product(it.id, it.name, it.description, it.price, it.imageUrl)
        }
    }
}
