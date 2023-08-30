@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.util.*

data class RestaurantResponse(
    val id: UUID,
    val managerId: String,
    val name: String,
    val availability: Availability,
    val menu: List<Product>,
    val location: Location
) {
    data class Product(
        val id: UUID,
        val name: String,
        val description: String?,
        val price: Double,
    )

    enum class Availability {
        OPEN, CLOSED
    }
}
