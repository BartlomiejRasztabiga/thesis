package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

import java.math.BigDecimal

data class WithdrawBalanceRequest(
    val amount: BigDecimal,
    val targetBankAccount: String
)
