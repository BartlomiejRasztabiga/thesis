package me.rasztabiga.thesis.order.domain.command.aggregate

import me.rasztabiga.thesis.order.domain.command.command.CreateDeliveryAddressCommand
import me.rasztabiga.thesis.order.domain.command.command.CreateUserCommand
import me.rasztabiga.thesis.order.domain.command.command.DeleteDeliveryAddressCommand
import me.rasztabiga.thesis.order.domain.command.event.DeliveryAddressCreatedEvent
import me.rasztabiga.thesis.order.domain.command.event.DeliveryAddressDeletedEvent
import me.rasztabiga.thesis.shared.domain.command.event.UserCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
internal class User {

    // TODO wszystko z tego jest potrzebne?
    @AggregateIdentifier
    private lateinit var id: String
    private lateinit var name: String

    @AggregateMember
    private val deliveryAddresses: MutableList<DeliveryAddress> = mutableListOf()

    private constructor()

    @CommandHandler
    constructor(command: CreateUserCommand) {
        apply(UserCreatedEvent(userId = command.id, name = command.name))
    }

    @CommandHandler
    fun handle(command: CreateDeliveryAddressCommand) {
        val addressId = command.addressId
        if (deliveryAddresses.any { it.addressId == addressId }) {
            error("Address with id $addressId already exists")
        }

        apply(
            DeliveryAddressCreatedEvent(
                userId = command.userId,
                addressId = command.addressId,
                address = command.address,
                additionalInfo = command.additionalInfo
            )
        )
    }

    @CommandHandler
    fun handle(command: DeleteDeliveryAddressCommand) {
        val addressId = command.addressId
        deliveryAddresses.find { it.addressId == addressId }?.let {
            apply(
                DeliveryAddressDeletedEvent(
                    userId = command.userId,
                    addressId = command.addressId
                )
            )
        }
    }

    @EventSourcingHandler
    fun on(event: UserCreatedEvent) {
        this.id = event.userId
        this.name = event.name
    }

    @EventSourcingHandler
    fun on(event: DeliveryAddressCreatedEvent) {
        this.deliveryAddresses.add(
            DeliveryAddress(
                addressId = event.addressId,
                address = event.address,
                additionalInfo = event.additionalInfo
            )
        )
    }

    @EventSourcingHandler
    fun on(event: DeliveryAddressDeletedEvent) {
        this.deliveryAddresses.removeIf { it.addressId == event.addressId }
    }
}
