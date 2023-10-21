package me.rasztabiga.thesis.restaurant.adapter.out

import me.rasztabiga.thesis.restaurant.domain.command.port.WithdrawalPort
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class FakeWithdrawalAdapter : WithdrawalPort {

    override fun withdraw(amount: BigDecimal, targetBankAccount: String) {
        // do nothing
    }
}
