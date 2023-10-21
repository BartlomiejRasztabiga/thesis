@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.restaurant.domain.command.command.PayPaymentCommand
import java.util.*

object PaymentControllerMapper {
    fun mapToPayPaymentCommand(paymentId: UUID): PayPaymentCommand {
        return PayPaymentCommand(
            paymentId = paymentId
        )
    }
}
