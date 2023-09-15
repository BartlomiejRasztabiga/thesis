package me.rasztabiga.thesis.query.domain.query.entity

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.util.*

@Document(collection = "order")
data class OrderEntity(
    @Id
    val id: UUID,
    val restaurantId: UUID,
    val restaurantLocation: Location,
    val userId: String,
    var status: OrderStatus,
    val items: MutableList<OrderItem>,
    var total: BigDecimal?,
    var paymentId: UUID?,
    var paymentSessionUrl: String?,
    var deliveryAddressId: UUID?,
    var deliveryLocation: Location?,
    var courierId: String?
) {
    enum class OrderStatus {
        CREATED,
        CANCELED,
        FINALIZED,
        PAID,
        CONFIRMED,
        REJECTED,
        PREPARED,
        PICKED_UP,
        DELIVERED
    }

    data class OrderItem(
        val id: UUID,
        val productId: UUID
    )
}
