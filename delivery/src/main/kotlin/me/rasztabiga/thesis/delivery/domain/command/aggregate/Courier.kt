package me.rasztabiga.thesis.delivery.domain.command.aggregate

import me.rasztabiga.thesis.delivery.domain.command.command.CreateCourierCommand
import me.rasztabiga.thesis.delivery.domain.command.command.UpdateCourierAvailabilityCommand
import me.rasztabiga.thesis.shared.domain.command.event.CourierAvailabilityUpdatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.CourierCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
internal class Courier {

    // TODO wszystko z tego jest potrzebne?
    @AggregateIdentifier
    private lateinit var id: String
    private lateinit var name: String
    private var availability: Availability = Availability.OFFLINE

    constructor()

    @CommandHandler
    constructor(command: CreateCourierCommand) {
        apply(CourierCreatedEvent(courierId = command.id, name = command.name, email = command.email))
    }

    @CommandHandler
    fun handle(command: UpdateCourierAvailabilityCommand) {
        apply(
            CourierAvailabilityUpdatedEvent(
                id = command.id,
                availability = CourierAvailabilityUpdatedEvent.Availability.valueOf(command.availability.name)
            )
        )
    }

    @EventSourcingHandler
    fun on(event: CourierCreatedEvent) {
        this.id = event.courierId
        this.name = event.name
    }
}
