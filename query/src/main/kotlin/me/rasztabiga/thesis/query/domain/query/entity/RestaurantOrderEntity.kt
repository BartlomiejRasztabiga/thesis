package me.rasztabiga.thesis.query.domain.query.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "restaurant_order")
data class RestaurantOrderEntity(
    @Id
    val id: UUID,
    val orderId: UUID,
    val restaurantId: UUID,
    val items: List<OrderItem>,
    var status: OrderStatus
) {
    enum class OrderStatus {
        NEW, ACCEPTED, PREPARED, REJECTED, PICKED_UP
    }

    data class OrderItem(
        val productId: UUID
    )
}
