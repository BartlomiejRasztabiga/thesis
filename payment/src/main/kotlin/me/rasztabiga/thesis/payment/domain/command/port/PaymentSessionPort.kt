package me.rasztabiga.thesis.payment.domain.command.port

import java.net.URL
import java.util.UUID

interface PaymentSessionPort {

    fun createPaymentSession(orderId: UUID): URL
}
