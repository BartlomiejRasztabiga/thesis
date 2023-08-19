package me.rasztabiga.thesis.delivery.domain.command.aggregate

import me.rasztabiga.thesis.delivery.domain.command.command.RejectDeliveryOfferCommand
import me.rasztabiga.thesis.delivery.domain.command.event.OrderDeliveryCreatedEvent
import me.rasztabiga.thesis.delivery.domain.command.event.OrderDeliveryRejectedEvent
import me.rasztabiga.thesis.delivery.domain.command.port.CalculateDeliveryFeePort
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderDeliveryOfferCommand
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal
import java.util.*

@Aggregate
class OrderDelivery {

    // TODO wszystko z tego jest potrzebne?
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var orderId: UUID
    private lateinit var restaurantAddress: String
    private lateinit var deliveryAddress: String
    private lateinit var status: DeliveryStatus
    private lateinit var courierFee: BigDecimal
    @Suppress("UnusedPrivateProperty")
    private var courierId: String? = null

    private constructor()

    @CommandHandler
    constructor(command: CreateOrderDeliveryOfferCommand, calculateDeliveryFeePort: CalculateDeliveryFeePort) {
        val baseFee = calculateDeliveryFeePort.calculateBaseFee(command.restaurantAddress, command.deliveryAddress)

        apply(
            OrderDeliveryCreatedEvent(
                deliveryId = command.id,
                orderId = command.orderId,
                restaurantAddress = command.restaurantAddress,
                deliveryAddress = command.deliveryAddress,
                courierFee = baseFee
            )
        )
    }

    @CommandHandler
    fun handle(command: RejectDeliveryOfferCommand) {
        require(this.status == DeliveryStatus.OFFER) { "Delivery can be rejected only if it's in OFFER status." }

        apply(
            OrderDeliveryRejectedEvent(
                deliveryId = command.id,
                courierId = command.courierId
            )
        )
    }

    @EventSourcingHandler
    fun on(event: OrderDeliveryCreatedEvent) {
        this.id = event.deliveryId
        this.orderId = event.orderId
        this.restaurantAddress = event.restaurantAddress
        this.deliveryAddress = event.deliveryAddress
        this.status = DeliveryStatus.OFFER
        this.courierFee = event.courierFee
    }
}
