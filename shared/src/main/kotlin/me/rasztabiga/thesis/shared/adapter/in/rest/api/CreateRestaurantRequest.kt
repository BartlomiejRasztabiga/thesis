@file:Suppress("InvalidPackageDeclaration")
package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.util.UUID

data class CreateRestaurantRequest(
    val name: String,
    val address: String,
    val email: String,
    val imageUrl: String
)
