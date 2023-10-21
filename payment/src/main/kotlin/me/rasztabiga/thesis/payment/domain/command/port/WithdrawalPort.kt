package me.rasztabiga.thesis.payment.domain.command.port

import java.math.BigDecimal

interface WithdrawalPort {

    fun withdraw(amount: BigDecimal, targetBankAccount: String)
}
