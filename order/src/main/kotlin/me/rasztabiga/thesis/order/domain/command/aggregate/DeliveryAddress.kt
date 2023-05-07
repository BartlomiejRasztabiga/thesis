package me.rasztabiga.thesis.order.domain.command.aggregate

import me.rasztabiga.thesis.order.domain.command.command.DeleteDeliveryAddressCommand
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.modelling.command.EntityId
import java.util.*
import org.axonframework.modelling.command.AggregateLifecycle.apply

internal class DeliveryAddress {

    @EntityId
    lateinit var addressId: UUID
        private set

    private lateinit var address: String
    private var additionalInfo: String? = null

    constructor(addressId: UUID, address: String, additionalInfo: String?) {
        this.addressId = addressId
        this.address = address
        this.additionalInfo = additionalInfo
    }
}

