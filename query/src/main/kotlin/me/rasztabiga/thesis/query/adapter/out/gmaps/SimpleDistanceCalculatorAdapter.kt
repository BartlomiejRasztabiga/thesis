package me.rasztabiga.thesis.query.adapter.out.gmaps

import me.rasztabiga.thesis.query.domain.query.port.DistanceCalculatorPort
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.springframework.stereotype.Service
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Service
class SimpleDistanceCalculatorAdapter : DistanceCalculatorPort {

    override fun calculateDistance(from: Location, to: Location): Double {
        val earthRadius = 6371.0 // earth radius in km

        val dLat = Math.toRadians(to.lat - from.lat)
        val dLng = Math.toRadians(to.lng - from.lng)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(from.lat)) * cos(Math.toRadians(to.lat)) *
                sin(dLng / 2) * sin(dLng / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c // distance in km
    }
}
