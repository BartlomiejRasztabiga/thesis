package me.rasztabiga.thesis.delivery.domain.command.aggregate

import me.rasztabiga.thesis.delivery.domain.command.command.AcceptDeliveryOfferCommand
import me.rasztabiga.thesis.delivery.domain.command.command.DeliverDeliveryCommand
import me.rasztabiga.thesis.delivery.domain.command.command.PickupDeliveryCommand
import me.rasztabiga.thesis.delivery.domain.command.command.RejectDeliveryOfferCommand
import me.rasztabiga.thesis.delivery.domain.command.port.CalculateDeliveryFeePort
import me.rasztabiga.thesis.delivery.domain.command.port.CourierOnlineVerifierPort
import me.rasztabiga.thesis.delivery.domain.command.port.OrderPreparedVerifierPort
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import me.rasztabiga.thesis.shared.domain.command.command.AssignDeliveryCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderDeliveryOfferCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryAssignedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryDeliveredEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryPickedUpEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryRejectedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal
import java.util.*

@Aggregate
class OrderDelivery {
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var orderId: UUID
    private lateinit var status: DeliveryStatus
    private lateinit var restaurantLocation: Location
    private lateinit var courierFee: BigDecimal
    private var courierId: String? = null

    private constructor()

    @CommandHandler
    constructor(command: CreateOrderDeliveryOfferCommand, calculateDeliveryFeePort: CalculateDeliveryFeePort) {
        val baseFee = calculateDeliveryFeePort.calculateBaseFee(
            command.restaurantLocation.streetAddress!!,
            command.deliveryLocation.streetAddress!!
        )

        this.courierFee = baseFee
        this.restaurantLocation = command.restaurantLocation

        apply(
            OrderDeliveryCreatedEvent(
                deliveryId = command.id,
                orderId = command.orderId,
                restaurantLocation = command.restaurantLocation,
                deliveryLocation = command.deliveryLocation,
                courierFee = baseFee
            )
        )
    }

    @CommandHandler
    fun handle(command: AssignDeliveryCommand) {
        require(this.status == DeliveryStatus.OFFER) { "Delivery can be assigned only if it's in OFFER status." }

        apply(
            OrderDeliveryAssignedEvent(
                deliveryId = command.id,
                courierId = command.courierId
            )
        )
    }

    @CommandHandler
    fun handle(command: RejectDeliveryOfferCommand, courierOnlineVerifierPort: CourierOnlineVerifierPort) {
        require(this.status == DeliveryStatus.ASSIGNED) { "Delivery can be rejected only if it's in ASSIGNED status." }
        require(this.courierId == command.courierId) {
            "Delivery can be rejected only by the courier who was assigned to it."
        }

        apply(
            OrderDeliveryRejectedEvent(
                deliveryId = command.id,
                courierId = command.courierId,
                restaurantLocation = restaurantLocation
            )
        )
    }

    @CommandHandler
    fun handle(command: AcceptDeliveryOfferCommand, courierOnlineVerifierPort: CourierOnlineVerifierPort) {
        require(this.status == DeliveryStatus.ASSIGNED) { "Delivery can be accepted only if it's in ASSIGNED status." }
        require(this.courierId == command.courierId) {
            "Delivery can be rejected only by the courier who was assigned to it."
        }

        apply(
            OrderDeliveryAcceptedEvent(
                deliveryId = command.id,
                orderId = this.orderId,
                courierId = command.courierId
            )
        )
    }

    @CommandHandler
    fun handle(
        command: PickupDeliveryCommand,
        orderPreparedVerifierPort: OrderPreparedVerifierPort,
        courierOnlineVerifierPort: CourierOnlineVerifierPort
    ) {
        require(this.status == DeliveryStatus.ACCEPTED) { "Delivery can be picked up only if it's in ACCEPTED status." }
        require(this.courierId == command.courierId) {
            "Delivery can be picked up only by the courier who accepted it."
        }
        require(orderPreparedVerifierPort.isOrderPrepared(this.orderId)) {
            "Delivery can be picked up only if the order is prepared."
        }

        apply(
            OrderDeliveryPickedUpEvent(
                deliveryId = command.id,
                orderId = this.orderId,
                courierId = command.courierId
            )
        )
    }

    @CommandHandler
    fun handle(command: DeliverDeliveryCommand, courierOnlineVerifierPort: CourierOnlineVerifierPort) {
        require(this.status == DeliveryStatus.PICKED_UP) {
            "Delivery can be delivered only if it's in PICKED_UP status."
        }
        require(this.courierId == command.courierId) {
            "Delivery can be delivered only by the courier who picked it up."
        }

        apply(
            OrderDeliveryDeliveredEvent(
                deliveryId = command.id,
                orderId = this.orderId,
                courierId = command.courierId
            )
        )
    }

    @EventSourcingHandler
    fun on(event: OrderDeliveryCreatedEvent) {
        this.id = event.deliveryId
        this.orderId = event.orderId
        this.status = DeliveryStatus.OFFER
    }

    @EventSourcingHandler
    fun on(event: OrderDeliveryAssignedEvent) {
        this.status = DeliveryStatus.ASSIGNED
        this.courierId = event.courierId
    }

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: OrderDeliveryAcceptedEvent) {
        this.status = DeliveryStatus.ACCEPTED
    }

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: OrderDeliveryRejectedEvent) {
        this.status = DeliveryStatus.OFFER
        this.courierId = null
    }

    @Suppress("UnusedParameter")
    @EventSourcingHandler
    fun on(event: OrderDeliveryPickedUpEvent) {
        this.status = DeliveryStatus.PICKED_UP
    }
}
