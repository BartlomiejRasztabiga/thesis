package me.rasztabiga.thesis.query.domain.query.entity

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "user")
data class UserEntity(
    @Id
    val id: String,
    val name: String,
    val email: String,
    val deliveryAddresses: MutableList<DeliveryAddress>,
    var defaultAddressId: UUID?
) {
    data class DeliveryAddress(
        val id: UUID,
        val location: Location
    )
}
