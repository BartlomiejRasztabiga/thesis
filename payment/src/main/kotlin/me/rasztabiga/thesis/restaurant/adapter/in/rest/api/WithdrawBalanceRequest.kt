package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api

import java.math.BigDecimal

data class WithdrawBalanceRequest(
    val amount: BigDecimal,
    val targetBankAccount: String
)
