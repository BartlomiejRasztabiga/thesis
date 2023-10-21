package me.rasztabiga.thesis.query.domain.query.entity

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.util.*

@Document(collection = "restaurant")
data class RestaurantEntity(
    @Id
    val id: UUID,
    val managerId: String,
    val name: String,
    val email: String,
    val availability: Availability,
    val menu: List<Product>,
    val location: Location,
    val imageUrl: String,
    val ratingsCount: Int,
    val ratingsAverage: Double
) {
    enum class Availability {
        OPEN,
        CLOSED
    }

    data class Product(
        val id: UUID,
        val name: String,
        val description: String?,
        val price: BigDecimal,
        val imageUrl: String
    )
}
