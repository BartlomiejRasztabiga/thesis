@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class PayeeResponse(
    val id: UUID,
    val userId: String,
    val name: String,
    val email: String,
    val balance: BigDecimal,
    val withdrawals: List<Withdrawal>
) {
    data class Withdrawal(
        val amount: BigDecimal,
        val accountNumber: String,
        val timestamp: Instant
    )
}
