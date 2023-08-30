@file:Suppress("InvalidPackageDeclaration")
package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api

import java.util.UUID

data class CreateRestaurantRequest(
    val id: UUID,
    val name: String,
    val address: String,
    val email: String
)
