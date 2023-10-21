@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api

import java.math.BigDecimal

data class UpdateRestaurantMenuRequest(
    val menu: List<Product>
) {
    data class Product(
        val name: String,
        val description: String?,
        val price: BigDecimal,
        val imageUrl: String
    )
}
