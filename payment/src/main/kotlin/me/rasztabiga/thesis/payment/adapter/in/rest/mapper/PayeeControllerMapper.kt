@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.payment.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.WithdrawBalanceRequest
import me.rasztabiga.thesis.shared.config.getUserId
import me.rasztabiga.thesis.shared.domain.command.command.WithdrawPayeeBalanceCommand
import org.springframework.web.server.ServerWebExchange
import java.util.*

object PayeeControllerMapper {
    fun mapToWithdrawBalanceCommand(
        payeeId: UUID,
        request: WithdrawBalanceRequest,
        exchange: ServerWebExchange
    ): WithdrawPayeeBalanceCommand {
        return WithdrawPayeeBalanceCommand(
            payeeId = payeeId,
            userId = exchange.getUserId(),
            amount = request.amount,
            targetBankAccount = request.targetBankAccount
        )
    }
}
