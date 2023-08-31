package me.rasztabiga.thesis.restaurant.domain.command.aggregate

import me.rasztabiga.thesis.shared.domain.command.command.AddPayeeBalanceCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreatePayeeCommand
import me.rasztabiga.thesis.shared.domain.command.event.PayeeCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal
import java.util.*

@Aggregate
internal class Payee {
    // either restaurant manager or courier, both have some balance and can withdraw money

    // TODO wszystko z tego jest potrzebne?
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var userId: String
    private lateinit var name: String
    private lateinit var email: String

    private lateinit var balance: BigDecimal

    private constructor()

    @CommandHandler
    constructor(command: CreatePayeeCommand) {
        apply(
            PayeeCreatedEvent(
                payeeId = command.id,
                userId = command.userId,
                name = command.name,
                email = command.email
            )
        )
    }

    @CommandHandler
    fun handle(command: AddPayeeBalanceCommand) {
        apply(
            PayeeBalanceAddedEvent(
                payeeId = command.payeeId,
                amount = command.amount
            )
        )
    }

    @EventSourcingHandler
    fun on(event: PayeeCreatedEvent) {
        this.id = event.payeeId
        this.userId = event.userId
        this.name = event.name
        this.email = event.email
        this.balance = BigDecimal.ZERO
    }

    @EventSourcingHandler
    fun on(event: PayeeBalanceAddedEvent) {
        this.balance = this.balance.add(event.amount)
    }

    // TODO withdraw
}
