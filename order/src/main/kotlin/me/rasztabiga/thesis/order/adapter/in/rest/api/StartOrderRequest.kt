@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest.api

import java.util.*

data class StartOrderRequest(
    val userId: String,
    val restaurantId: UUID
)
