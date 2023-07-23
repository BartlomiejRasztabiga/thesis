package me.rasztabiga.thesis.order.domain.command.aggregate

import org.axonframework.modelling.command.EntityId
import java.util.*

internal class OrderItem {

    @EntityId
    var orderItemId: UUID
        private set

    private var productId: UUID

    constructor(orderItemId: UUID, productId: UUID) {
        this.orderItemId = orderItemId
        this.productId = productId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderItem) return false

        if (orderItemId != other.orderItemId) return false
        return productId == other.productId
    }

    override fun hashCode(): Int {
        var result = orderItemId.hashCode()
        result = 31 * result + productId.hashCode()
        return result
    }
}
