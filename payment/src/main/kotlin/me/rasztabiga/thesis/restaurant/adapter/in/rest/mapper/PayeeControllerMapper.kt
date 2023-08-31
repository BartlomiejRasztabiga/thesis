@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.WithdrawBalanceRequest
import me.rasztabiga.thesis.restaurant.domain.command.command.PayPaymentCommand
import me.rasztabiga.thesis.shared.config.getUserId
import org.springframework.web.server.ServerWebExchange
import java.util.*

object PayeeControllerMapper {
    fun mapToWithdrawBalanceCommand(
        payeeId: UUID,
        request: WithdrawBalanceRequest,
        exchange: ServerWebExchange
    ): WithdrawPayeeBalanceCommand {
        return WithdrawPayeeBalanceCommand(
            paymentId = paymentId,
            payerId = exchange.getUserId()
        )
    }
}
