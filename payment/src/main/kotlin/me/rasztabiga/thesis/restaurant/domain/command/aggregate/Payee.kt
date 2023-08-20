package me.rasztabiga.thesis.restaurant.domain.command.aggregate

import me.rasztabiga.thesis.shared.domain.command.command.CreatePayeeCommand
import me.rasztabiga.thesis.shared.domain.command.event.PayeeCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
internal class Payee {

    // TODO wszystko z tego jest potrzebne?
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var userId: String

    private constructor()

    @CommandHandler
    constructor(command: CreatePayeeCommand) {
        apply(PayeeCreatedEvent(id = command.id, userId = command.userId))
    }

    @EventSourcingHandler
    fun on(event: PayeeCreatedEvent) {
        this.id = event.id
        this.userId = event.userId
    }
}
