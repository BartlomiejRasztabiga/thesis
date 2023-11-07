package me.rasztabiga.thesis.query.domain.query.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.*

@Document(collection = "restaurant_order")
data class RestaurantOrderEntity(
    @Id
    val id: UUID,
    @field:Indexed val orderId: UUID,
    @field:Indexed val restaurantId: UUID,
    val items: Map<UUID, Int>,
    var status: OrderStatus,
    val createdAt: Instant
) {
    enum class OrderStatus {
        NEW, ACCEPTED, PREPARED, REJECTED, PICKED_UP, DELIVERED
    }
}
