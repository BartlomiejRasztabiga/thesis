package me.rasztabiga.thesis.order.domain.query.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "order")
data class OrderEntity(
    @Id
    val id: UUID,
    val restaurantId: UUID,
    val userId: String,
    val status: OrderStatus
) {
    enum class OrderStatus {
        CREATED
    }
}
