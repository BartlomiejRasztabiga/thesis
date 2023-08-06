@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.restaurant.domain.command.command.PayPaymentCommand
import me.rasztabiga.thesis.shared.config.getUserId
import org.springframework.web.server.ServerWebExchange
import java.util.*

object PaymentControllerMapper {
    fun mapToPayPaymentCommand(paymentId: UUID, exchange: ServerWebExchange): PayPaymentCommand {
        return PayPaymentCommand(
            paymentId = paymentId,
            payeeId = exchange.getUserId()
        )
    }
}
