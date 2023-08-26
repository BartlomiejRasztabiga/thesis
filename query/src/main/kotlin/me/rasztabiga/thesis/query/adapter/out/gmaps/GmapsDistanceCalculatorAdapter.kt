package me.rasztabiga.thesis.query.adapter.out.gmaps

import me.rasztabiga.thesis.query.domain.query.port.DistanceCalculatorPort
import me.rasztabiga.thesis.shared.infrastructure.gmaps.GmapsClient
import org.springframework.stereotype.Service

@Service
class GmapsDistanceCalculatorAdapter(
    private val gmapsClient: GmapsClient
) : DistanceCalculatorPort {

    override fun calculateDistance(from: String, to: String): Double {
        return gmapsClient.getDistanceInMeters(from, to) / METERS_IN_KM
    }

    companion object {
        private const val METERS_IN_KM = 1000.0
    }
}
