@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.payment.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.payment.domain.command.command.PayPaymentCommand
import me.rasztabiga.thesis.shared.config.getUserId
import org.springframework.web.server.ServerWebExchange
import java.util.*

object PaymentControllerMapper {
    fun mapToPayPaymentCommand(paymentId: UUID): PayPaymentCommand {
        return PayPaymentCommand(
            paymentId = paymentId
        )
    }
}
