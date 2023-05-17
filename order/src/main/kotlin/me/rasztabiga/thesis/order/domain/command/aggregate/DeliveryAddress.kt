package me.rasztabiga.thesis.order.domain.command.aggregate

import org.axonframework.modelling.command.EntityId
import java.util.*

internal class DeliveryAddress {

    @EntityId
    var addressId: UUID
        private set

    private var address: String
    private var additionalInfo: String? = null

    constructor(addressId: UUID, address: String, additionalInfo: String?) {
        this.addressId = addressId
        this.address = address
        this.additionalInfo = additionalInfo
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DeliveryAddress) return false

        if (addressId != other.addressId) return false
        if (address != other.address) return false
        return additionalInfo == other.additionalInfo
    }

    override fun hashCode(): Int {
        var result = addressId.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + (additionalInfo?.hashCode() ?: 0)
        return result
    }
}

