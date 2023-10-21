package me.rasztabiga.thesis.restaurant.domain.command.port

import java.math.BigDecimal

interface WithdrawalPort {

    fun withdraw(amount: BigDecimal, targetBankAccount: String)
}
