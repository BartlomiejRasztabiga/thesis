package me.rasztabiga.thesis.restaurant.domain.command.port

import java.math.BigDecimal

interface CalculateDeliveryFeePort {

    fun calculateDeliveryFee(restaurantAddress: String, deliveryAddress: String): BigDecimal
}
