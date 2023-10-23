package me.rasztabiga.thesis.query.domain.query.entity

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Document(collection = "order")
data class OrderEntity(
    @Id
    val id: UUID,
    val restaurantId: UUID,
    val restaurantLocation: Location,
    val userId: String,
    var status: OrderStatus,
    val items: MutableMap<UUID, Int>,
    var total: BigDecimal?,
    var productsTotal: BigDecimal?,
    var deliveryFee: BigDecimal?,
    var paymentId: UUID?,
    var paymentSessionUrl: String?,
    var deliveryAddressId: UUID?,
    var deliveryLocation: Location?,
    var courierId: String?,
    val createdAt: Instant,
    val events: MutableList<OrderEvent>
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

    data class OrderEvent(
        val type: OrderEventType,
        val createdAt: Instant = Instant.now()
    ) {
        enum class OrderEventType {
            CONFIRMED,
            COURIER_ASSIGNED,
            PREPARED,
            PICKED_UP,
            DELIVERED
        }
    }
}
