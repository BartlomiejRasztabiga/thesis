package me.rasztabiga.thesis.query.domain.query.port

import java.math.BigDecimal
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location

interface CalculateDeliveryFeePort {

    fun calculateDeliveryFee(from: Location, to: Location): BigDecimal
}
