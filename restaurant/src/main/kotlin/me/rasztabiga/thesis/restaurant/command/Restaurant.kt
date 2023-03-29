package me.rasztabiga.thesis.restaurant.command

import me.rasztabiga.thesis.restaurant.api.command.CreateRestaurantCommand
import me.rasztabiga.thesis.restaurant.api.event.RestaurantCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.util.UUID

@Aggregate
internal class Restaurant {

    @AggregateIdentifier
    private lateinit var restaurantId: UUID
    private lateinit var name: String

    @CommandHandler
    constructor(command: CreateRestaurantCommand) {
        AggregateLifecycle.apply(
            RestaurantCreatedEvent(
                restaurantId = command.restaurantId,
                name = command.name
            )
        )
    }

    @EventSourcingHandler
    fun on(event: RestaurantCreatedEvent) {
        this.restaurantId = event.restaurantId
        this.name = event.name
    }
}
