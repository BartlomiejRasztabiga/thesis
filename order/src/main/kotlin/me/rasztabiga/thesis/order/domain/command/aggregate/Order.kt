package me.rasztabiga.thesis.order.domain.command.aggregate

import me.rasztabiga.thesis.order.domain.command.command.AddOrderItemCommand
import me.rasztabiga.thesis.order.domain.command.command.CancelOrderCommand
import me.rasztabiga.thesis.order.domain.command.command.DeleteOrderItemCommand
import me.rasztabiga.thesis.order.domain.command.command.FinalizeOrderCommand
import me.rasztabiga.thesis.order.domain.command.command.MarkOrderAsPaidCommand
import me.rasztabiga.thesis.order.domain.command.command.RejectOrderCommand
import me.rasztabiga.thesis.order.domain.command.command.StartOrderCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderCanceledEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderFinalizedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderItemAddedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderItemDeletedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderRejectedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderStartedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaidEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Suppress("TooManyFunctions")
@Aggregate
internal class Order {

    // TODO wszystko z tego jest potrzebne?
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var userId: String
    private lateinit var restaurantId: UUID
    private lateinit var status: OrderStatus

    @AggregateMember
    private val items: MutableList<OrderItem> = mutableListOf()

    private constructor()

    @CommandHandler
    constructor(command: StartOrderCommand) {
        apply(
            OrderStartedEvent(
                orderId = command.orderId,
                restaurantId = command.restaurantId,
                userId = command.userId,
                status = OrderStartedEvent.OrderStatus.CREATED
            )
        )
    }

    @CommandHandler
    fun handle(command: CancelOrderCommand) {
        require(this.userId == command.userId) { "Order can be canceled only by the user who created it." }
        require(
            this.status in setOf(
                OrderStatus.CREATED,
                OrderStatus.FINALIZED
            )
        ) { "Order can be canceled only if it's in CREATED or FINALIZED status." }

        apply(
            OrderCanceledEvent(
                orderId = command.orderId
            )
        )
    }

    @CommandHandler
    fun handle(command: AddOrderItemCommand) {
        require(this.userId == command.userId) { "Order can be updated only by the user who created it." }
        require(this.status == OrderStatus.CREATED) { "Order can be updated only if it's in CREATED status." }

        apply(
            OrderItemAddedEvent(
                orderId = command.orderId,
                orderItemId = command.orderItemId,
                productId = command.productId
            )
        )
    }

    @CommandHandler
    fun handle(command: DeleteOrderItemCommand) {
        require(this.userId == command.userId) { "Order can be updated only by the user who created it." }
        require(this.status == OrderStatus.CREATED) { "Order can be updated only if it's in CREATED status." }

        this.items.find { it.orderItemId == command.orderItemId }?.let {
            apply(
                OrderItemDeletedEvent(
                    orderId = command.orderId,
                    orderItemId = command.orderItemId
                )
            )
        }
    }

    @CommandHandler
    fun handle(command: FinalizeOrderCommand) {
        require(this.userId == command.userId) { "Order can be finalized only by the user who created it." }
        require(this.status == OrderStatus.CREATED) { "Order can be finalized only if it's in CREATED status." }

        apply(
            OrderFinalizedEvent(
                orderId = command.orderId,
                userId = command.userId,
                restaurantId = this.restaurantId,
                items = this.items.map {
                    OrderFinalizedEvent.OrderItem(
                        orderItemId = it.orderItemId,
                        productId = it.productId
                    )
                },
                deliveryAddressId = command.deliveryAddressId
            )
        )
    }

    // TODO is it required?
    @CommandHandler
    fun handle(command: MarkOrderAsPaidCommand) {
        require(this.userId == command.userId) { "Order can be marked as paid only by the user who created it." }
        require(this.status == OrderStatus.FINALIZED) {
            "Order can be marked as paid only if it's in FINALIZED status."
        }

        apply(
            OrderPaidEvent(
                orderId = command.orderId
            )
        )
    }

    // TODO is it required?
    @CommandHandler
    fun handle(command: RejectOrderCommand) {
        require(this.status == OrderStatus.PAID) { "Order can be rejected only if it's in PAID status." }

        apply(
            OrderRejectedEvent(
                orderId = command.orderId
            )
        )
    }

    @EventSourcingHandler
    fun on(event: OrderStartedEvent) {
        this.id = event.orderId
        this.userId = event.userId
        this.restaurantId = event.restaurantId
        this.status = OrderStatus.valueOf(event.status.name)
    }

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: OrderCanceledEvent) {
        this.status = OrderStatus.CANCELED
    }

    @EventSourcingHandler
    fun on(event: OrderItemAddedEvent) {
        this.items.add(OrderItem(event.orderItemId, event.productId))
    }

    @EventSourcingHandler
    fun on(event: OrderItemDeletedEvent) {
        this.items.removeIf { it.orderItemId == event.orderItemId }
    }

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: OrderFinalizedEvent) {
        this.status = OrderStatus.FINALIZED
    }

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: OrderPaidEvent) {
        this.status = OrderStatus.PAID
    }
}
