package me.rasztabiga.thesis.query.domain.query.entity

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Document(collection = "order_delivery")
data class OrderDeliveryEntity(
    @Id
    val id: UUID,
    val orderId: UUID,
    val restaurantLocation: Location,
    val deliveryLocation: Location,
    @field:Indexed var status: DeliveryStatus,
    val courierFee: BigDecimal,
    @field:Indexed var courierId: String?,
    val createdAt: Instant
)
