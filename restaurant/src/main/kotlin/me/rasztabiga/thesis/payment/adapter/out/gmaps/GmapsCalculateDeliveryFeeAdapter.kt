package me.rasztabiga.thesis.payment.adapter.out.gmaps

import me.rasztabiga.thesis.payment.domain.command.port.CalculateDeliveryFeePort
import me.rasztabiga.thesis.shared.infrastructure.gmaps.GmapsClient
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class GmapsCalculateDeliveryFeeAdapter(
    private val gmapsClient: GmapsClient
) : CalculateDeliveryFeePort {

    @Suppress("MagicNumber")
    override fun calculateDeliveryFee(restaurantAddress: String, deliveryAddress: String): BigDecimal {
        val distanceInMeters = gmapsClient.getDistanceInMeters(restaurantAddress, deliveryAddress).toBigDecimal()
        val fee = distanceInMeters * FEE_PER_KM / METERS_IN_KM
        return fee.setScale(2, RoundingMode.HALF_UP)
    }

    companion object {
        private val METERS_IN_KM = 1000.0.toBigDecimal()
        private val FEE_PER_KM = 3.toBigDecimal()
    }
}
