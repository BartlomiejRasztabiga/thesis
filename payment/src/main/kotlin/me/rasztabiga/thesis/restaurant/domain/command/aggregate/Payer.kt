package me.rasztabiga.thesis.restaurant.domain.command.aggregate

import me.rasztabiga.thesis.shared.domain.command.command.CreatePayerCommand
import me.rasztabiga.thesis.shared.domain.command.event.PayerCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
internal class Payer {

    // TODO wszystko z tego jest potrzebne?
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var userId: String

    private constructor()

    @CommandHandler
    constructor(command: CreatePayerCommand) {
        apply(PayerCreatedEvent(id = command.id, userId = command.userId))
    }

    @EventSourcingHandler
    fun on(event: PayerCreatedEvent) {
        this.id = event.id
        this.userId = event.userId
    }
}
