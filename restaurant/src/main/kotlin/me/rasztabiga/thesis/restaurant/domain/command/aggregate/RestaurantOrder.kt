package me.rasztabiga.thesis.restaurant.domain.command.aggregate

import me.rasztabiga.thesis.restaurant.domain.command.command.AcceptRestaurantOrderCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.PrepareRestaurantOrderCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.RejectRestaurantOrderCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreateRestaurantOrderCommand
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderPreparedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderRejectedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
internal class RestaurantOrder {

    // TODO wszystko z tego jest potrzebne?
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var orderId: UUID // TODO remove?
    private lateinit var status: OrderStatus

    private constructor()

    @CommandHandler
    constructor(command: CreateRestaurantOrderCommand) {
        apply(
            RestaurantOrderCreatedEvent(
                restaurantOrderId = command.restaurantOrderId,
                orderId = command.orderId,
                items = command.items.map {
                    RestaurantOrderCreatedEvent.OrderItem(
                        productId = it.productId
                    )
                },
                restaurantId = command.restaurantId
            )
        )
    }

    @CommandHandler
    fun handle(command: AcceptRestaurantOrderCommand) {
        require(this.status == OrderStatus.NEW) { "Order can be accepted only if it's in NEW status." }

        apply(
            RestaurantOrderAcceptedEvent(
                restaurantOrderId = command.restaurantOrderId,
                orderId = this.orderId,
                restaurantId = command.restaurantId
            )
        )
    }

    @CommandHandler
    fun handle(command: RejectRestaurantOrderCommand) {
        require(this.status == OrderStatus.NEW) { "Order can be rejected only if it's in NEW status." }

        apply(
            RestaurantOrderRejectedEvent(
                restaurantOrderId = command.restaurantOrderId,
                orderId = this.orderId
            )
        )
    }

    @CommandHandler
    fun handle(command: PrepareRestaurantOrderCommand) {
        require(this.status == OrderStatus.ACCEPTED) { "Order can be prepared only if it's in ACCEPTED status." }

        apply(
            RestaurantOrderPreparedEvent(
                restaurantOrderId = command.restaurantOrderId,
                orderId = this.orderId,
                restaurantId = command.restaurantId
            )
        )
    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderCreatedEvent) {
        this.id = event.restaurantOrderId
        this.orderId = event.orderId
        this.status = OrderStatus.NEW
    }

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: RestaurantOrderAcceptedEvent) {
        this.status = OrderStatus.ACCEPTED
    }

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: RestaurantOrderRejectedEvent) {
        this.status = OrderStatus.REJECTED
    }

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: RestaurantOrderPreparedEvent) {
        this.status = OrderStatus.PREPARED
    }

    enum class OrderStatus {
        NEW, ACCEPTED, REJECTED, PREPARED
    }
}
