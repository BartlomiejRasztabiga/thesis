package me.rasztabiga.thesis.restaurant.domain.query.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "restaurant")
data class RestaurantEntity(
    @Id
    val id: UUID,
    val name: String,
    val availability: Availability,
    val menu: List<Product>
) {
    enum class Availability {
        OPEN,
        CLOSED
    }

    data class Product(
        val id: UUID,
        val name: String,
        val description: String?,
        val price: Double,
    )
}
