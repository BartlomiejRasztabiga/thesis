package me.rasztabiga.thesis.payment.adapter.out

import me.rasztabiga.thesis.payment.domain.command.port.WithdrawalPort
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class FakeWithdrawalAdapter : WithdrawalPort {

    override fun withdraw(amount: BigDecimal, targetBankAccount: String) {
        // do nothing
    }
}
