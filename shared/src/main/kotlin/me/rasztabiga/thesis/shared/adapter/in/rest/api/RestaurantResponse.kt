@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.util.*

data class RestaurantResponse(
    val id: UUID,
    val name: String,
    val availability: Availability,
    val menu: List<Product>
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
