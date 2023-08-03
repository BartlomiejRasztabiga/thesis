package me.rasztabiga.thesis.order.domain.command.aggregate

import me.rasztabiga.thesis.order.domain.command.command.AddOrderItemCommand
import me.rasztabiga.thesis.order.domain.command.command.CancelOrderCommand
import me.rasztabiga.thesis.order.domain.command.command.DeleteOrderItemCommand
import me.rasztabiga.thesis.order.domain.command.command.StartOrderCommand
import me.rasztabiga.thesis.order.domain.command.event.OrderCanceledEvent
import me.rasztabiga.thesis.order.domain.command.event.OrderItemAddedEvent
import me.rasztabiga.thesis.order.domain.command.event.OrderItemDeletedEvent
import me.rasztabiga.thesis.order.domain.command.event.OrderStartedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
internal class Order {
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var userId: String
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
                status = OrderStatus.CREATED
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

    @EventSourcingHandler
    fun on(event: OrderStartedEvent) {
        this.id = event.orderId
        this.userId = event.userId
        this.status = event.status
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
}
