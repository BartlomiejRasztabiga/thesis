@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.math.BigDecimal
import java.util.*

data class PayeeResponse(
    val id: UUID,
    val userId: String,
    val name: String,
    val email: String,
    val balance: BigDecimal
)
