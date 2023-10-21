package me.rasztabiga.thesis.restaurant.domain.command.port

import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderPaymentCommand

interface PaymentSessionPort {

    fun createPaymentSession(command: CreateOrderPaymentCommand): String
}
