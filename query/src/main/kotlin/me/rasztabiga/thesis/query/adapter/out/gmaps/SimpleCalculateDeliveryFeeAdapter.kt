package me.rasztabiga.thesis.query.adapter.out.gmaps

import me.rasztabiga.thesis.query.domain.query.port.CalculateDeliveryFeePort
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class SimpleCalculateDeliveryFeeAdapter(
    private val simpleDistanceCalculatorAdapter: SimpleDistanceCalculatorAdapter
) : CalculateDeliveryFeePort {

    override fun calculateDeliveryFee(from: Location, to: Location): BigDecimal {
        val distanceInKms = simpleDistanceCalculatorAdapter.calculateDistance(from, to).toBigDecimal()
        val fee = distanceInKms * FEE_PER_KM
        return fee.setScale(2, RoundingMode.HALF_UP)
    }

    companion object {
        private val FEE_PER_KM = 3.toBigDecimal()
    }
}
