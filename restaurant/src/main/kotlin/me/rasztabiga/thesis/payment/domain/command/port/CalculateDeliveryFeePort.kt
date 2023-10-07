package me.rasztabiga.thesis.payment.domain.command.port

import java.math.BigDecimal

interface CalculateDeliveryFeePort {

    fun calculateDeliveryFee(restaurantAddress: String, deliveryAddress: String): BigDecimal
}
