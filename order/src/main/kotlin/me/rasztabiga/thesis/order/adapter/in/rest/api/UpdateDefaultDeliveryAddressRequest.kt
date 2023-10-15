@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest.api

import java.util.UUID

data class UpdateDefaultDeliveryAddressRequest(
    val addressId: UUID
)
