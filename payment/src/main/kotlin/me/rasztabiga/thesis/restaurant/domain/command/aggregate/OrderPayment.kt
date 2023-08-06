package me.rasztabiga.thesis.restaurant.domain.command.aggregate

import me.rasztabiga.thesis.shared.domain.command.command.CreatePaymentCommand
import me.rasztabiga.thesis.shared.domain.command.event.PaymentCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal
import java.util.*

// TODO move to Payee aggregate?
@Aggregate
internal class OrderPayment {

    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var payeeId: String
    private lateinit var orderId: UUID
    private lateinit var amount: BigDecimal

    private constructor()

    @CommandHandler
    constructor(command: CreatePaymentCommand) {
        apply(
            PaymentCreatedEvent(
                id = command.id,
                orderId = command.orderId,
                payeeId = command.payeeId,
                amount = command.amount
            )
        )
    }

    @EventSourcingHandler
    fun on(event: PaymentCreatedEvent) {
        this.id = event.id
        this.orderId = event.orderId
        this.payeeId = event.payeeId
        this.amount = event.amount
    }
}
