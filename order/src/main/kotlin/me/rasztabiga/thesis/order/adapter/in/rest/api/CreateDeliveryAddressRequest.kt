@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest.api

data class CreateDeliveryAddressRequest(
    val address: String,
    val additionalInfo: String?
)
