package me.rasztabiga.thesis.delivery.domain.command.port

import java.math.BigDecimal

interface CalculateDeliveryFeePort {

    fun calculateBaseFee(restaurantAddress: String, deliveryAddress: String): BigDecimal
}
