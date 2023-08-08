package me.rasztabiga.thesis.restaurant.domain.command.aggregate

import me.rasztabiga.thesis.restaurant.domain.command.command.AcceptRestaurantOrderCommand
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantOrderCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.command.CreateRestaurantOrderCommand
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderAcceptedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
internal class RestaurantOrder {
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var status: OrderStatus

    private constructor()

    @CommandHandler
    constructor(command: CreateRestaurantOrderCommand) {
        apply(
            RestaurantOrderCreatedEvent(
                orderId = command.orderId
            )
        )
    }

    @CommandHandler
    fun handle(command: AcceptRestaurantOrderCommand) {
        apply(
            RestaurantOrderAcceptedEvent(
                orderId = command.orderId,
                restaurantId = command.restaurantId
            )
        )
    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderCreatedEvent) {
        this.id = event.orderId
        this.status = OrderStatus.NEW
    }

    enum class OrderStatus {
        NEW
    }
}
