package me.rasztabiga.thesis.query.domain.query.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.util.*

@Document(collection = "order_delivery")
data class OrderDeliveryEntity(
    @Id
    val id: UUID,
    val orderId: UUID,
    val restaurantAddress: String,
    val deliveryAddress: String,
    var status: DeliveryStatus,
    val courierFee: BigDecimal,
    var courierId: String?,
    val courierIdsDeclined: MutableList<String>
)
