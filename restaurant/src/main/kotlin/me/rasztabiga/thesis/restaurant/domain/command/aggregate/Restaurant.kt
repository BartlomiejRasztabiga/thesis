package me.rasztabiga.thesis.restaurant.domain.command.aggregate

import me.rasztabiga.thesis.restaurant.domain.command.command.CreateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.DeleteRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.UpdateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantDeletedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantUpdatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateLifecycle.markDeleted
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
internal class Restaurant {

    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var name: String

    constructor()

    @CommandHandler
    constructor(command: CreateRestaurantCommand) {
        apply(RestaurantCreatedEvent(id = command.id, name = command.name))
    }

    @CommandHandler
    fun handle(command: UpdateRestaurantCommand) {
        apply(RestaurantUpdatedEvent(id = command.id, name = command.name))
    }

    @CommandHandler
    fun handle(command: DeleteRestaurantCommand) {
        apply(RestaurantDeletedEvent(id = command.id))
    }

    @EventSourcingHandler
    fun on(event: RestaurantCreatedEvent) {
        this.id = event.id
        this.name = event.name
    }

    @EventSourcingHandler
    fun on(event: RestaurantUpdatedEvent) {
        this.name = event.name
    }

    @EventSourcingHandler
    fun on(event: RestaurantDeletedEvent) {
        markDeleted()
    }
}
