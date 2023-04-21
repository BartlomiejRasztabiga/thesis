@file:Suppress("InvalidPackageDeclaration")
package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api

import java.util.UUID

data class RestaurantResponse(
    val id: UUID,
    val name: String
)
