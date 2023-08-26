package me.rasztabiga.thesis.delivery.adapter.out.fake

import me.rasztabiga.thesis.delivery.domain.command.port.CalculateDeliveryFeePort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
@Profile("!gmaps")
class FakeCalculateDeliveryFeeAdapter : CalculateDeliveryFeePort {

    @Suppress("MagicNumber")
    override fun calculateBaseFee(restaurantAddress: String, deliveryAddress: String): BigDecimal {
        val a = restaurantAddress.length
        val b = deliveryAddress.length
        return BigDecimal.valueOf((a + b) * 0.1)
    }
}
