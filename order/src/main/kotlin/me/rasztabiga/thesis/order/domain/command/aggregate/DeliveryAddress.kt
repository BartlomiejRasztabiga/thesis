package me.rasztabiga.thesis.order.domain.command.aggregate

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.axonframework.modelling.command.EntityId
import java.util.*

internal class DeliveryAddress {

    @EntityId
    var addressId: UUID
        private set

    private var location: Location

    constructor(addressId: UUID, location: Location) {
        this.addressId = addressId
        this.location = location
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DeliveryAddress) return false

        if (addressId != other.addressId) return false
        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int {
        var result = addressId.hashCode()
        result = 31 * result + location.hashCode()
        return result
    }
}
