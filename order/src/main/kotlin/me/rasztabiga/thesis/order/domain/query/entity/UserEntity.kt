package me.rasztabiga.thesis.order.domain.query.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "user")
data class UserEntity(
    @Id
    val id: String,
    val name: String,
    val deliveryAddresses: MutableList<DeliveryAddress>
) {
    data class DeliveryAddress(
        val id: UUID,
        val address: String,
        val additionalInfo: String?
    )
}
