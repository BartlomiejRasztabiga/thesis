package me.rasztabiga.thesis.restaurant.domain.command.aggregate

import me.rasztabiga.thesis.restaurant.domain.command.command.PayPaymentCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderPaymentCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaymentCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.PaymentPaidEvent
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
    private lateinit var status: PaymentStatus

    private constructor()

    @CommandHandler
    constructor(command: CreateOrderPaymentCommand) {
        apply(
            OrderPaymentCreatedEvent(
                id = command.id,
                orderId = command.orderId,
                payeeId = command.payeeId,
                amount = command.amount
            )
        )
    }

    @CommandHandler
    fun handle(command: PayPaymentCommand) {
        require(this.payeeId == command.payeeId) { "Payment can be paid only by the user who created it." }
        require(this.status == PaymentStatus.NEW) { "Payment can be paid only if it's in NEW status." }

        // TODO call payment gateway

        apply(
            PaymentPaidEvent(
                paymentId = command.paymentId,
                orderId = this.orderId
            )
        )
    }

    @EventSourcingHandler
    fun on(event: OrderPaymentCreatedEvent) {
        this.id = event.id
        this.orderId = event.orderId
        this.payeeId = event.payeeId
        this.amount = event.amount
        this.status = PaymentStatus.NEW
    }

    enum class PaymentStatus {
        NEW, PAID, FAILED
    }
}
