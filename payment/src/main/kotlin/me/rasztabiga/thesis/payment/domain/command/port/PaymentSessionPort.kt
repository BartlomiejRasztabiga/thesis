package me.rasztabiga.thesis.payment.domain.command.port

import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderPaymentCommand

interface PaymentSessionPort {

    fun createPaymentSession(command: CreateOrderPaymentCommand): String
}
