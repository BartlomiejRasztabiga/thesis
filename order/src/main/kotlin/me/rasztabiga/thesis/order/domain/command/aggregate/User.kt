package me.rasztabiga.thesis.order.domain.command.aggregate

import me.rasztabiga.thesis.order.domain.command.command.CreateUserCommand
import me.rasztabiga.thesis.order.domain.command.event.UserCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import org.axonframework.modelling.command.AggregateLifecycle.apply

@Aggregate
internal class User {

    @AggregateIdentifier
    private lateinit var id: String
    private lateinit var name: String

    constructor()

    @CommandHandler
    constructor(command: CreateUserCommand) {
        apply(UserCreatedEvent(id = command.id, name = command.name))
    }

    @EventSourcingHandler
    fun on(event: UserCreatedEvent) {
        this.id = event.id
        this.name = event.name
    }
}
