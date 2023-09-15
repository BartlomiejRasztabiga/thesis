package me.rasztabiga.thesis.payment.domain.command.aggregate

import me.rasztabiga.thesis.payment.domain.command.command.PayPaymentCommand
import me.rasztabiga.thesis.payment.domain.command.event.OrderPaymentDeletedEvent
import me.rasztabiga.thesis.payment.domain.command.port.PaymentSessionPort
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderPaymentCommand
import me.rasztabiga.thesis.shared.domain.command.command.DeleteOrderPaymentCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaymentCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaymentPaidEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateLifecycle.markDeleted
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal
import java.util.*

// TODO move to Payer aggregate?
@Aggregate
internal class OrderPayment {

    // TODO wszystko z tego jest potrzebne?
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var payerId: String
    private lateinit var orderId: UUID // TODO remove?
    private lateinit var amount: BigDecimal
    private lateinit var status: PaymentStatus

    private constructor()

    @CommandHandler
    constructor(command: CreateOrderPaymentCommand, paymentSessionPort: PaymentSessionPort) {
        // TODO stripe client, save url or sth
        paymentSessionPort.createPaymentSession(command.orderId)

        apply(
            OrderPaymentCreatedEvent(
                id = command.id,
                orderId = command.orderId,
                payerId = command.payerId,
                amount = command.amount
            )
        )
    }

    @CommandHandler
    fun handle(command: PayPaymentCommand) {
        require(this.payerId == command.payerId) { "Payment can be paid only by the user who created it." }
        require(this.status == PaymentStatus.NEW) { "Payment can be paid only if it's in NEW status." }

        // TODO call payment gateway

        apply(
            OrderPaymentPaidEvent(
                paymentId = command.paymentId,
                orderId = this.orderId
            )
        )
    }

    @CommandHandler
    fun handle(command: DeleteOrderPaymentCommand) {
        apply(
            OrderPaymentDeletedEvent(
                id = command.paymentId
            )
        )
    }

    @EventSourcingHandler
    fun on(event: OrderPaymentCreatedEvent) {
        this.id = event.id
        this.orderId = event.orderId
        this.payerId = event.payerId
        this.amount = event.amount
        this.status = PaymentStatus.NEW
    }

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: OrderPaymentDeletedEvent) {
        markDeleted()
    }

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: OrderPaymentPaidEvent) {
        this.status = PaymentStatus.PAID
    }

    enum class PaymentStatus {
        NEW, PAID, FAILED
    }
}
