package me.rasztabiga.thesis.delivery.adapter.out.gmaps

import me.rasztabiga.thesis.delivery.domain.command.port.CalculateDeliveryFeePort
import me.rasztabiga.thesis.shared.infrastructure.gmaps.GmapsClient
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class GmapsCalculateDeliveryFeeAdapter(
    private val gmapsClient: GmapsClient
) : CalculateDeliveryFeePort {

    @Suppress("MagicNumber")
    override fun calculateBaseFee(restaurantAddress: String, deliveryAddress: String): BigDecimal {
        val distanceInMeters = gmapsClient.getDistanceInMeters(restaurantAddress, deliveryAddress)
        val feePerKm = 2.2
        return BigDecimal.valueOf(distanceInMeters * feePerKm / METERS_IN_KM)
    }

    companion object {
        private const val METERS_IN_KM = 1000
    }
}
