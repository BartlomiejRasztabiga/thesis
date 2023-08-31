package me.rasztabiga.thesis.payment.adapter.`in`.rest.api

import java.math.BigDecimal

data class WithdrawBalanceRequest(
    val amount: BigDecimal,
    val targetBankAccount: String
)
