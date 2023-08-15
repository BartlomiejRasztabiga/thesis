package me.rasztabiga.thesis.delivery.domain.command.aggregate

import me.rasztabiga.thesis.delivery.domain.command.command.CreateCourierCommand
import me.rasztabiga.thesis.delivery.domain.command.command.UpdateCourierAvailabilityCommand
import me.rasztabiga.thesis.delivery.domain.command.event.CourierAvailabilityUpdatedEvent
import me.rasztabiga.thesis.delivery.domain.command.event.CourierCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
internal class Courier {

    @AggregateIdentifier
    private lateinit var id: String
    private lateinit var name: String
    private var availability: Availability = Availability.OFFLINE

    constructor()

    @CommandHandler
    constructor(command: CreateCourierCommand) {
        apply(CourierCreatedEvent(id = command.id, name = command.name))
    }

    @CommandHandler
    fun handle(command: UpdateCourierAvailabilityCommand) {
        apply(CourierAvailabilityUpdatedEvent(id = command.id, availability = command.availability))
    }

    @EventSourcingHandler
    fun on(event: CourierCreatedEvent) {
        this.id = event.id
        this.name = event.name
    }
}
