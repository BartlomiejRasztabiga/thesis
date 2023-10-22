package me.rasztabiga.thesis.order.domain.command.aggregate

import me.rasztabiga.thesis.order.domain.command.command.AddOrderItemCommand
import me.rasztabiga.thesis.order.domain.command.command.CancelOrderCommand
import me.rasztabiga.thesis.order.domain.command.command.DeleteOrderItemCommand
import me.rasztabiga.thesis.order.domain.command.command.FinalizeOrderCommand
import me.rasztabiga.thesis.order.domain.command.command.RateOrderCommand
import me.rasztabiga.thesis.order.domain.command.command.StartOrderCommand
import me.rasztabiga.thesis.order.domain.command.port.OrderVerificationPort
import me.rasztabiga.thesis.shared.domain.command.command.MarkOrderAsDeliveredCommand
import me.rasztabiga.thesis.shared.domain.command.command.MarkOrderAsPaidCommand
import me.rasztabiga.thesis.shared.domain.command.command.RejectOrderCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderCanceledEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveredEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderFinalizedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderItemAddedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderItemDeletedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaidEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderRatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderRejectedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderStartedEvent
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
    private val items: MutableMap<UUID, Int> = mutableMapOf()

    private constructor()

    @CommandHandler
    constructor(command: StartOrderCommand, orderVerificationPort: OrderVerificationPort) {
        require(orderVerificationPort.restaurantExists(command.restaurantId)) {
            "Restaurant with id ${command.restaurantId} does not exist"
        }
        require(orderVerificationPort.isRestaurantOpen(command.restaurantId)) {
            "Restaurant with id ${command.restaurantId} is closed"
        }
        require(orderVerificationPort.userExists(command.userId)) {
            "User with id ${command.userId} does not exist"
        }

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
                productId = command.productId
            )
        )
    }

    @CommandHandler
    fun handle(command: DeleteOrderItemCommand) {
        require(this.userId == command.userId) { "Order can be updated only by the user who created it." }
        require(this.status == OrderStatus.CREATED) { "Order can be updated only if it's in CREATED status." }
        require(this.items.containsKey(command.productId)) {
            "Order does not contain product with id ${command.productId}"
        }

        if (this.items[command.productId]!! >= 1) {
            apply(
                OrderItemDeletedEvent(
                    orderId = command.orderId,
                    productId = command.productId
                )
            )
        }
    }

    @CommandHandler
    fun handle(command: FinalizeOrderCommand, orderVerificationPort: OrderVerificationPort) {
        require(this.userId == command.userId) { "Order can be finalized only by the user who created it." }
        require(this.status == OrderStatus.CREATED) { "Order can be finalized only if it's in CREATED status." }

        this.items.forEach {
            require(orderVerificationPort.productExists(it.key, this.restaurantId)) {
                "Product with id ${it.key} does not exist in restaurant with id ${this.restaurantId}"
            }
        }

        apply(
            OrderFinalizedEvent(
                orderId = command.orderId,
                userId = command.userId,
                restaurantId = this.restaurantId,
                items = this.items
            )
        )
    }

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

    @CommandHandler
    fun handle(command: MarkOrderAsDeliveredCommand) {
        require(this.status == OrderStatus.PAID) {
            "Order can be marked as delivered only if it's in PAID status."
        }

        apply(
            OrderDeliveredEvent(
                orderId = command.orderId
            )
        )
    }

    @CommandHandler
    fun handle(command: RejectOrderCommand) {
        require(this.status == OrderStatus.PAID) { "Order can be rejected only if it's in PAID status." }

        apply(
            OrderRejectedEvent(
                orderId = command.orderId
            )
        )
    }

    @Suppress("MagicNumber")
    @CommandHandler
    fun handle(command: RateOrderCommand) {
        require(this.status == OrderStatus.DELIVERED) { "Order can be rated only if it's in DELIVERED status." }
        require(this.userId == command.userId) { "Order can be rated only by the user who created it." }
        require(command.rating in 1..5) { "Rating must be between 1 and 5" }

        apply(
            OrderRatedEvent(
                orderId = this.id,
                userId = this.userId,
                restaurantId = this.restaurantId,
                rating = command.rating
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
        if (this.items.containsKey(event.productId)) {
            this.items[event.productId] = this.items[event.productId]!! + 1
        } else {
            this.items[event.productId] = 1
        }
    }

    @EventSourcingHandler
    fun on(event: OrderItemDeletedEvent) {
        if (this.items[event.productId]!! > 1) {
            this.items[event.productId] = this.items[event.productId]!! - 1
        } else {
            this.items.remove(event.productId)
        }
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

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: OrderDeliveredEvent) {
        this.status = OrderStatus.DELIVERED
    }
}
