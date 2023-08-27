@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.util.*

data class UserResponse(
    val id: String,
    val name: String,
    val deliveryAddresses: List<DeliveryAddress>
) {
    data class DeliveryAddress(
        val id: UUID,
        val location: Location
    )
}
