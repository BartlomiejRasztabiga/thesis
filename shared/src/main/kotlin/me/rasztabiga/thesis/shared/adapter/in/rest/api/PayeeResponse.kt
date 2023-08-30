@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.math.BigDecimal

data class PayeeResponse(
    val id: String,
    val userId: String,
    val name: String,
    val email: String,
    val balance: BigDecimal
)
