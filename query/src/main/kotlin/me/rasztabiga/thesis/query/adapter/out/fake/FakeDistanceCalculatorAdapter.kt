package me.rasztabiga.thesis.query.adapter.out.fake

import me.rasztabiga.thesis.query.domain.query.port.DistanceCalculatorPort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!gmaps")
class FakeDistanceCalculatorAdapter : DistanceCalculatorPort {

    override fun calculateDistance(from: String, to: String): Double {
        val a = from.length
        val b = to.length
        return ((a + b) / 2).toDouble()
    }
}
