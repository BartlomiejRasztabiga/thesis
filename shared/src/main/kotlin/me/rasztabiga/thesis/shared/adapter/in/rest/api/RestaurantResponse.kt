@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.math.BigDecimal
import java.util.*

data class RestaurantResponse(
    val id: UUID,
    val managerId: String,
    val email: String,
    val name: String,
    val availability: Availability,
    val menu: List<Product>,
    val location: Location,
    val imageUrl: String,
    val avgRating: Double,
    val deliveryFee: BigDecimal?
) {
    data class Product(
        val id: UUID,
        val name: String,
        val description: String?,
        val price: BigDecimal,
    )

    enum class Availability {
        OPEN, CLOSED
    }
}
