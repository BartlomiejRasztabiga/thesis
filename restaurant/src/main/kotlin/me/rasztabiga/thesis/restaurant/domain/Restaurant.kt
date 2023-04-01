package me.rasztabiga.thesis.restaurant.domain

import me.rasztabiga.thesis.restaurant.domain.command.CreateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.event.RestaurantCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.util.UUID

@Aggregate
internal class Restaurant {

    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var name: String

    @CommandHandler
    constructor(command: CreateRestaurantCommand) {
        AggregateLifecycle.apply(
            RestaurantCreatedEvent(
                id = command.id,
                name = command.name
            )
        )
    }

    @EventSourcingHandler
    fun on(event: RestaurantCreatedEvent) {
        this.id = event.id
        this.name = event.name
    }
}
