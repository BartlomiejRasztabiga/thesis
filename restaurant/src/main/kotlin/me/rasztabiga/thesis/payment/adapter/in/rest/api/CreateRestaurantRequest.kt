@file:Suppress("InvalidPackageDeclaration")
package me.rasztabiga.thesis.payment.adapter.`in`.rest.api

import java.util.UUID

data class CreateRestaurantRequest(
    val id: UUID,
    val name: String,
    val address: String,
    val email: String,
    val imageUrl: String
)
